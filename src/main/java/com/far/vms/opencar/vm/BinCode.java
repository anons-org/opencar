package com.far.vms.opencar.vm;


/*
 * @description: 存储代码
 * @author mike/Fang.J
 * @data 2022/11/17
*/
import java.util.ArrayList;
import java.util.List;

public class BinCode {

    private List<Integer> codes = new ArrayList<>();

    public void loadBin(BinFile file){
        while (file.available() > 0) {
            int code = file.readInt();
            codes.add(code);
        }
    }

    public int get(int pc){
        return codes.get(pc);
    }


}
