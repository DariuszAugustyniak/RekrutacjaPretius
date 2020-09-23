import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;


public class main {




    public static void main(String[] args) {
        Integer all = 0;
        Integer intoDev = 0;
        Integer intoTest = 0;

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



                    if(FilenameUtils.getExtension(filename.toString()).equals("xml")){

                        Path source = home.resolve(filename);
                        Path target = dev.resolve(filename);
                        Files.move(source, target, REPLACE_EXISTING);
                        String fileData = "All " + ++all + " Dev " + ++intoDev + " Test " + intoTest;
                        Files.write(Paths.get(count.getPath()),fileData.getBytes());

                    }
                    if(FilenameUtils.getExtension(filename.toString()).equals("jar")){


                        Path source = home.resolve(filename);
                        FileTime time = Files.getLastModifiedTime(source);

                        if(time.toMillis()%2==0){
                            Path target = dev.resolve(filename);
                            Files.move(source, target, REPLACE_EXISTING);

                            String fileData = "All " + ++all + " Dev " + ++intoDev + " Test " + intoTest;
                            Files.write(Paths.get(count.getPath()),fileData.getBytes());
                        }else{
                            Path target = test.resolve(filename);
                            Files.move(source, target, REPLACE_EXISTING);

                            String fileData = "All " + ++all + " Dev " + intoDev + " Test " + ++intoTest;
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
