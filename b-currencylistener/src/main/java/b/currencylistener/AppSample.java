/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package b.currencylistener;

import b.currencylistener.verticles.FutureSampleVerticle;
import b.currencylistener.verticles.SampleVerticle;
import com.mybaas.AppRunner;

public class AppSample {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args){
        try {
            AppRunner.initInstance().setVerticlesToRun(FutureSampleVerticle.class).Dispatch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
