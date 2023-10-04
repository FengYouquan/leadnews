package com.youquan.common.tess4j;

import lombok.Data;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/12 11:25
 */
@Data
@Component
@ConfigurationProperties(prefix = "tess4j")
public class Tess4jClient {
    private String dataPath;
    private String language;

    public String doOcr(BufferedImage bufferedImage) throws TesseractException {
        // 创建Tesseract对象
        ITesseract tesseract = new Tesseract();
        // 设置字体库路径
        tesseract.setDatapath(dataPath);
        // 中文识别
        tesseract.setLanguage(language);
        // 执行ocr识别
        String result = tesseract.doOCR(bufferedImage);
        // 替换回车和tal键  使结果为一行
        result = result.replaceAll("[\\r|\\n]", "-").replaceAll(" ", "");
        return result;
    }
}
