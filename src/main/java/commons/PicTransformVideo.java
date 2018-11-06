package commons;

import org.jim2mov.core.*;
import org.jim2mov.utils.MovieUtils;
import org.junit.Test;

import java.io.*;
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
        picToVideo("d:\\jpg","test5.avi",3,1440,860,"d:\\testjms");
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
        Map<String,Object> map = new HashMap<>();
        File file =null;
        try {
            String filePath=convertPicToAvi(jpgDirPath, aviFileName, fps, mWidth, mHeight);
            String path =filePath.replace("file:","");
            try {
                if (savePath!=null&&savePath!=""){
                    //保存文件
                    saveFile(savePath+"\\"+aviFileName,path);
                    map.put("videoPath",savePath);
                }else{
                    map.put("videoPath",path);
                }
            }catch (Exception e){
                map.put("msg","转存视频失败:"+e.getMessage());
                e.printStackTrace();
            }
        } catch (MovieSaveException e) {
            map.put("code",1);
            map.put("msg","转换视频失败:"+e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    public String convertPicToAvi (String jpgDirPath, String aviFileName,int fps, int mWidth, int mHeight)throws MovieSaveException {
        // jpgs目录放置jpg图片,图片文件名为(1.jpg,2.jpg...)
        final File[] jpgs = new File(jpgDirPath).listFiles();
        if(jpgs==null || jpgs.length==0){
            //返回1图片文件为空
            return "1";
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
        return String.valueOf(dmip.getMediaLocator());
    }

    /**
     * 另存文件
     * @param fileName 另存文件路径及名称
     * @param path 原文件路径
     * @return
     */
    private String saveFile( String fileName,String path) {

        OutputStream os = null;
        InputStream  is=null;
        try {
            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件

            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            File file = new File(fileName);
            is= new FileInputStream(tempFile);
            os = new FileOutputStream(file);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            return "0";
        } catch (IOException e) {
            e.printStackTrace();
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "2";
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
