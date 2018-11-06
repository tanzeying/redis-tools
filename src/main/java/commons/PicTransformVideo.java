package commons;

import org.jim2mov.core.*;
import org.jim2mov.utils.MovieUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PicTransformVideo{
//    public static void main(String[] args) throws Exception {
//        String jpgDirPath = "d:\\jpg"; // jpg文件夹路径
//        String aviFileName = "test5.avi"; // 生成的avi视频文件名（生成路径为本工程）
//        int fps = 1; // 每秒播放的帧数
//        int mWidth = 320; // 视频的宽度
//        int mHeight = 240; // 视频的高度
//        convertPicToAvi(jpgDirPath, aviFileName, fps, mWidth, mHeight);
//    }

    @Test
    public void testPicToVideo(){
        picToVideo("d:\\jpg","test5.avi",3,1440,860,null);
    }

    /**
     * 当前仅支持jpg转换
     * @param jpgDirPath 文件位置
     * @param aviFileName 生成视频名称及视频格式
     * @param fps   每秒帧数
     * @param mWidth 视频宽 默认为1440
     * @param mHeight 视频高 默认为860
     * @return
     */
    public Map<String,Object> picToVideo(String jpgDirPath, String aviFileName, int fps, int mWidth, int mHeight,String savePath){
        convertPicToAvi(jpgDirPath, aviFileName, fps, mWidth, mHeight,savePath);
        Map<String,Object> map = new HashMap<>();
        return map;
    }

    public void convertPicToAvi(String jpgDirPath, String aviFileName,int fps, int mWidth, int mHeight, String savePath) {
        // jpgs目录放置jpg图片,图片文件名为(1.jpg,2.jpg...)
        final File[] jpgs = new File(jpgDirPath).listFiles();
        if(jpgs==null || jpgs.length==0){
            return;
        }

        // 对文件名进行排序(本示例假定文件名中的数字越小,生成视频的帧数越靠前)
        Arrays.sort(jpgs, new Comparator<File>() {
            public int compare(File file1, File file2) {
                String numberName1 = file1.getName().replace(".jpg", "");
                String numberName2 = file2.getName().replace(".jpg", "");
                return new Integer(numberName1) - new Integer(numberName2);
            }
        });

        // 生成视频的名称
        DefaultMovieInfoProvider dmip = new DefaultMovieInfoProvider(aviFileName);
        // 设置每秒帧数
        dmip.setFPS(fps>0?fps:3); // 如果未设置，默认为3
        // 设置总帧数
        dmip.setNumberOfFrames(jpgs.length);
        // 设置视频宽和高（最好与图片宽高保持一直）
        dmip.setMWidth(mWidth>0?mWidth:1440); // 如果未设置，默认为1440
        dmip.setMHeight(mHeight>0?mHeight:860); // 如果未设置，默认为860

        try {
            new Jim2Mov(new ImageProvider() {
                public byte[] getImage(int frame) {
                    try {
                        // 设置压缩比
                        return MovieUtils.convertImageToJPEG((jpgs[frame]), 1.0f);
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                    return null;
                }
            }, dmip, null).saveMovie(MovieInfoProvider.TYPE_QUICKTIME_JPEG);
        } catch (MovieSaveException e) {
            System.err.println(e);
        }

    }


}
