/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cbmodel;

/**
 *
 * @author msv
 */
public class CBItem {
    int id;
    String name;
    Double param1;
    Integer param2;

    public int getId(){
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString(){
        return this.name;
    }

    public Double  getParam1(){
        return this.param1;
    }
    public void setParam1(Double param) {
        this.param1 = param;
    }
    public Integer  getParam2(){
        return this.param2;
    }
    public void setParam2(Integer param) {
        this.param2 = param;
    }
}
