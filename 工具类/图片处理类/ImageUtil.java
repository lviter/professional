import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

/**
 *
 * 合并图片
 */
@Slf4j
public class ImageUtil {

    /**
     * 1MB
     */
    private static final int minMb = 1024*1024;
    /**
     * 5MB
     */
    private static final int maxMb = 5*1024*1024;

    /**
     * 压缩图片
     * @param source
     * @param targetW
     * @param targetH
     * @return
     */
    private static BufferedImage resize(BufferedImage source, int targetW, int targetH) {
        // targetW，targetH分别表示目标长和宽
        int type = source.getType();
        BufferedImage target = null;
        int width = source.getWidth();
        int height = source.getHeight();
        double sx = (double) targetW / width;
        double sy = (double) targetH / height;
        // 这里想实现在targetW，targetH范围内实现等比缩放
        if (sx > sy) {
            sx = sy;
            targetW = (int) (sx * source.getWidth());
        } else {
            sy = sx;
            targetH = (int) (sy * source.getHeight());
        }
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
                    targetH);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage(targetW, targetH, type);
        }
        Graphics2D g = target.createGraphics();
        // smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

    /**
     * 合成两张图片，对图片没有要求，原来的图片是怎么样的合成的图片就是怎么样的
     * @param img1
     * @param img2
     * @return
     */
    private static BufferedImage mergeImages(BufferedImage img1,BufferedImage img2) {
        int h1 = img1.getHeight();
        int w1 = img1.getWidth();
        int h2 = img2.getHeight();
        int w2 = img2.getWidth();
        Graphics2D graphics2D = null;
        // 生成新图片
        BufferedImage destImage = null;
        destImage = new BufferedImage(w1 + w2, Math.max(h1,h2), BufferedImage.TYPE_INT_RGB);
        graphics2D = destImage.createGraphics();
        if (h1>=h2) {
            graphics2D.drawImage(img1, 0, 0, w1, h1, null);
            //计算第二张图片的位置
            int y = (h1-h2)/2;
            graphics2D.drawImage(img2, w1, y, w2, h2, null);
        } else {
            int y = (h2-h1)/2;
            graphics2D.drawImage(img1,0,y,w1,h1,null);
            graphics2D.drawImage(img2,w1,0,w2,h2,null);
        }
        graphics2D.dispose();
        return destImage;
    }



    /**
     * 待合并的两张张图必须满足这样的前提，如果水平方向合并，则高度必须相等；如果是垂直方向合并，宽度必须相等。
     *
     * @param img1 待合并的第一张图
     * @param img2 带合并的第二张图
     * @return 返回合并后的BufferedImage对象
     */
    private static BufferedImage mergeImage(BufferedImage img1, BufferedImage img2) {
        int w1 = img1.getWidth();
        int h1 = img1.getHeight();
        int w2 = img2.getWidth();
        Graphics2D graphics2D = null;
        // 生成新图片
        BufferedImage destImage = null;
        destImage = new BufferedImage(w1 + w2, h1, BufferedImage.TYPE_INT_RGB);
        graphics2D = destImage.createGraphics();
        graphics2D.drawImage(img1, 0, 0, w1, h1, null);
        graphics2D.drawImage(img2, w1, 0, w2, h1, null);
        graphics2D.dispose();
        return destImage;
    }

    /**
     * 生成新图片到本地
     */
    private static void writeImageLocal(String newImageUrl, BufferedImage img) {
        if (newImageUrl != null && img != null) {
            try {
                File outputfile = new File(newImageUrl);
                ImageIO.write(img, "jpg", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * BufferImage 转成inputStream
     * @param img
     * @return
     */
    private static InputStream toInputStream(BufferedImage img) {
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        try {
             ImageIO.write(img, "jpg", out);
             InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
             return  inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 合并两张图片 两张图片压缩到以第一张图片长度和高度
     * @param sourceImageUrl1 为基准图片，合并后的大小以这张图片为准
     * @param sourceImageUrl2 被合并的图片
     * @return 返回一个流对象
     */
   public static InputStream toMergeImage(String sourceImageUrl1,String sourceImageUrl2) {
        try {
            BufferedImage readImage1 = ImageIO.read(new FileInputStream(sourceImageUrl1));
            BufferedImage readImage2 = ImageIO.read(new FileInputStream(sourceImageUrl2));
            int width = readImage1.getWidth();
            int height = readImage1.getHeight();
            readImage2 = resize(readImage2,width,height);
            BufferedImage bufferedImage = mergeImage(readImage1, readImage2);
            InputStream inputStream = toInputStream(bufferedImage);
            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
   }

    /**
     * 图片不压缩，合并两张图片
     * @param sourceImageUrl1 第一张出现合并图片的左边
     * @param sourceImageUrl2 第二张出现合并图片的右边
     * @return 返回一个流
     */
   public static InputStream getInputStream(InputStream sourceImageUrl1, InputStream sourceImageUrl2) {
       try {
           BufferedImage readImage1 = ImageIO.read(sourceImageUrl1);
           BufferedImage readImage2 = ImageIO.read(sourceImageUrl2);
           BufferedImage bufferedImage = mergeImages(readImage1, readImage2);
           InputStream inputStream = toInputStream(bufferedImage);
           return inputStream;
       } catch (Exception e) {
           log.error("合成图片失败", e);
       }
      return null;
   }

    /**
     * 合成后的图片保存到本地一份
     * @param fileInputStream
     * @param fileName
     * @return
     */
   public static File getFile(InputStream fileInputStream, String fileName) {
       File file = null;
       OutputStream os = null;
       try {
           file = File.createTempFile("", fileName);
           //下载
           os = new FileOutputStream(file);

           int bytesRead = 0;
           byte[] buffer = new byte[8192];
           while ((bytesRead = fileInputStream.read(buffer, 0, 8192)) != -1) {
               os.write(buffer, 0, bytesRead);
           }
       } catch (Exception e) {
           log.error("创建本地图片失败！", e);
       } finally {
           try {
               if (fileInputStream != null) {
                   fileInputStream.close();
               }
               if (os != null) {
                   os.close();
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       return file;
   }

    /**
     * 将图片以同比的 2倍 、3倍 压缩或者不压缩
     * @param inputStream
     * @param filePath
     * @return 文件
     */
    public static File getFileByCompression(InputStream inputStream,String filePath){
        try {
            int available = inputStream.available();
            BufferedImage image = ImageIO.read(inputStream);
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);
            BufferedImage resize;
            if (available >= minMb && available <= maxMb) {
                //图片以2倍同比压缩
                resize = resize(bufferedImage, bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);
            } else if (available >= maxMb) {
                // 图片以三倍同比压缩
                resize = resize(bufferedImage, bufferedImage.getWidth() / 3, bufferedImage.getHeight() / 3);
            } else {
                //不压缩
                resize = resize(bufferedImage,bufferedImage.getWidth(),bufferedImage.getHeight());
            }
            InputStream is = toInputStream(resize);
            return getFile(is,filePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
