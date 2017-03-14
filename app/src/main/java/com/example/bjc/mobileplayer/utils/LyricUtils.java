package com.example.bjc.mobileplayer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by 毕静存 on 2017/1/20.
 */

public class LyricUtils {


//    [00:01.98]胡桃夹子 (《健忘村》电影推广曲) - 张碧晨
//    [00:05.21]词：张碧晨
//    [00:06.03]曲：张碧晨
//    [00:06.85]编曲：刘卓/邢迟
//    [00:08.17]钢琴：刘卓
//    [00:11.45]和声编写：Brandy Tien
//    [00:12.56]和声：Brandy Tien

    public void readLyricFile(File file){
        if(file==null||!file.exists()){
            //文件不存在
        }else{
            //文件存在
            BufferedReader bufferedReader=null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
                String line = "";//读取的一行数据；
                while((line=bufferedReader.readLine())!=null){
                    line=parseLyric(line);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 解析歌词
     * @param line  [00:09.00][00:09.00][00:09.00]弦乐：亚洲爱乐乐团/首席/李朋
     * @return
     */
    private String parseLyric(String line) {

        int first = line.indexOf("[");//有返回0，没有返回-1；
        int last = line.indexOf("]");//有返回0，没有返回-1；
        if(first==0&&last!=-1){//肯定有一句歌词
            long[] times=new long[getCount(line)];//创建一个装时间的数组；
            String stringTime=line.substring(first+1,last);
            times[0]=String2long(stringTime);
        }


        return "";
    }

    /**
     * 将字符串变成long类型；
     * @param stringTime  00:09.00
     * @return
     */
    private long String2long(String stringTime) {
        long result=-1;
        String[] s0 = stringTime.split(":");// 00
        String[] s1 = stringTime.split("\\.");// 09 00
        //分
        long min = Long.parseLong(s0[0]);
        //秒
        long second = Long.parseLong(s1[0]);
        //毫秒
        long mil = Long.parseLong(s1[1]);

        result=min*60*1000+second*1000+mil*10;
        return result;
    }

    /**
     * 获取一句歌词中的所有时间搓
     * @param line
     * @return
     */
    private int getCount(String line) {
        int result=-1;
        String[] left = line.split("\\[");
        String[] right = line.split("\\]");
        if(left.length==0&&right.length==0){
            result=1;
        }else if(left.length>right.length){
            result=left.length;
        }else {
            result=right.length;
        }
        return result;
    }
}
