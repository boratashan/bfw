package b;


import b.downloadclock.verticles.DownloadRequestEmitterVerticle;
import com.mybaas.AppRunner;

public class AppRunDownloadClock {
    public static void main(String[] args) {
        try {
            AppRunner.initInstance()
                    .setVerticlesToRun(DownloadRequestEmitterVerticle.class)
                    .Dispatch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
