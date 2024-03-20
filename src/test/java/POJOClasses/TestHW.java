package POJOClasses;

import java.util.ArrayList;

public class TestHW {
    private ArrayList<Homework> hwList;
    
    public ArrayList<Homework> getHwList() {
        return hwList;
    }
    
    public void setHwList(ArrayList<Homework> hwList) {
        this.hwList = hwList;
    }
    
    @Override
    public String toString() {
        return "Test{" +
                "hwList=" + hwList +
                '}';
    }
}
