import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class main {




    public static void main(String[] args) {
        Integer wszystkie = 0;
        Integer doDev = 0;
        Integer doTest = 0;

        Path home = Paths.get("./HOME");
        Path test = Paths.get("./TEST");
        Path dev = Paths.get("./DEV");

        File count;

        try {
            Files.createDirectories(home);
            Files.createDirectories(test);
            Files.createDirectories(dev);
            count = new File ("./HOME/count.txt");
            count.createNewFile();


            WatchService homeWatcher = FileSystems.getDefault().newWatchService();
            home.register(homeWatcher,ENTRY_CREATE);
            WatchKey key;
            while ((key = homeWatcher.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();

                    System.out.println("File affected: " + event.context() + "." + " Extention" + FilenameUtils.getExtension(filename.toString()));

                    if(FilenameUtils.getExtension(filename.toString()).equals("xml")){
                        System.out.println("zgadza sie xml");
                        Path source = home.resolve(filename);
                        Path target = dev.resolve(filename);
                        Files.move(source, target, REPLACE_EXISTING);
                        String fileData = "All " + ++wszystkie + " Dev " + ++doDev + " Test " + doTest;
                        Files.write(Paths.get(count.getPath()),fileData.getBytes());

                    }
                    if(FilenameUtils.getExtension(filename.toString()).equals("jar")){

                        System.out.println("zgadza sie jar");
                        Path source = home.resolve(filename);
                        FileTime time = Files.getLastModifiedTime(source);
                        System.out.println(time.toMillis());
                        if(time.toMillis()%2==0){
                            Path target = dev.resolve(filename);
                            Files.move(source, target, REPLACE_EXISTING);
                            System.out.println("do dev");
                            String fileData = "All " + ++wszystkie + " Dev " + ++doDev + " Test " + doTest;
                            Files.write(Paths.get(count.getPath()),fileData.getBytes());
                        }else{
                            Path target = test.resolve(filename);
                            Files.move(source, target, REPLACE_EXISTING);
                            System.out.println("do test");
                            String fileData = "All " + ++wszystkie + " Dev " + doDev + " Test " + ++doTest;
                            Files.write(Paths.get(count.getPath()),fileData.getBytes());
                        }

                    }
                }


                key.reset();
            }

        }catch (IOException e){

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
