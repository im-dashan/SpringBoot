import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成二维码demo类
 */
public class QRCodeTest {
    public static void main(String[] args) throws Exception {
        Map<EncodeHintType, Object> qrMap = new HashMap<>();
        qrMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        String url = "https://img1.baidu.com/it/u=3708085132,1797335777&fm=253&fmt=auto&app=120&f=JPEG?w=690&h=388";
        // 矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200, qrMap);

        String filePath = "E://";
        String fileName = "qrcode.png";

        Path path = FileSystems.getDefault().getPath(filePath, fileName);

        MatrixToImageWriter.writeToPath(bitMatrix, "png", path);

    }
}
