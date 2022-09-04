package work;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {
    public static void main(String[] args) throws URISyntaxException {
        String loc = new File(test.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File loca = new File(loc);
        File loca2 = new File(loca.getParent());
        File loca3 = new File(loca2.getParent());
        String path = loca3.getParent()+"\\Input\\landcover.asc";

        System.out.println(Files.exists(Paths.get(path)));
    }
}
