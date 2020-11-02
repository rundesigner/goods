package plugin.factory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.*;
import tools.Messages;

/**
 * @author MZEG
 */
public class Plugin {

    public static Integer onStartSystem = 0;
    public static String methodName = "";

    public static void CallMethod(String methodName1) {
        methodName = methodName1;
        //Вызываем плагины ядра
        List<Class<?>> plugCoreClasses = Plugin.getCoreClasses("plugins");
        if (plugCoreClasses != null) {
            for (final Class<?> plugClass : plugCoreClasses) {
                try {
                    //      Messages.show("Метод="+methodName+"| Класс="+plugClass.getName());
                    Method method = plugClass.getMethod(methodName);
                    Object instance = plugClass.newInstance();
                    method.invoke(instance);
                } catch (Exception e) {
                    Messages.consolemode = 1;
                    Messages.sboi(e);
                    Messages.consolemode = 0;
                }
            }
        }
        //Вызываем внешние плагины
        List<Class<?>> plugClasses = Plugin.getPluginClasses(methodName);
        if (plugClasses != null) {
            for (final Class<?> plugClass : plugClasses) {
                try {
                    Method method = plugClass.getMethod(methodName);
                    Object instance = plugClass.newInstance();
                    method.invoke(instance);
                } catch (Exception e) {
                    Messages.sboi(e);
                }
            }
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static List<Class<?>> getCoreClasses(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert classLoader != null;
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            List<Class<?>> classes = new ArrayList<Class<?>>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
            return classes;
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return null;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return null;
        }
         ClassLoader classLoader = Plugin.class.getClassLoader();
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String finded_classname = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                if (finded_classname.lastIndexOf("$") == -1) {
                    
                    final Class<?> plugClass = classLoader.loadClass(finded_classname);
                    try {
                        plugClass.getMethod(methodName);
                        classes.add(Class.forName(finded_classname));
                        Messages.show("Нашли класс="+finded_classname);
                    } catch (NoSuchMethodException e) {
                    }

                }
            }
        }
        return classes;
    }

    // Метод возвращает список классов плагинов, в которых реализован
    // метод, переданный в качестве параметра
    public static List<Class<?>> getPluginClasses(String methodName) {
        List<Class<?>> result = new ArrayList<Class<?>>();

        File pluginDir = new File("plugins");
        if (!pluginDir.isDirectory()) {
            return null;
        }
        File[] jars = pluginDir.listFiles(new FileFilter() {

            public boolean accept(File file) {
                return file.isFile()
                        && file.getName().endsWith(".jar");
            }
        });
        //Подгружаем плагины из внешних файлов
        if (jars.length == 0) {
            return null;
        }
        for (File jar : jars) {
            try {
                URL jarURL = jar.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL});
                JarFile jarFile = new JarFile(jar);
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    String className = jarEntries.nextElement().getName();
                    if (!className.endsWith(".class")) {
                        continue;
                    }
                    className = className.replaceAll("/", ".");
                    className = className.replaceAll(".class", "");
                    final Class<?> plugClass = classLoader.loadClass(className);
                    try {
                        plugClass.getMethod(methodName);
                        result.add(plugClass);
                    } catch (NoSuchMethodException e) {
                    }
                }
            } catch (Exception e) {
                Messages.sboi(e);
            }
        }
        return result;
    }
}
