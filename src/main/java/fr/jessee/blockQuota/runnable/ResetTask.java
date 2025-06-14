package fr.jessee.blockQuota.runnable;

import fr.jessee.blockQuota.BlockQuota;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;

public class ResetTask extends BukkitRunnable {

    @Override
    public void run() {
        LocalTime now = LocalTime.now();
        LocalTime reset = LocalTime.parse(BlockQuota.getInstance().getMainConfig().getString("reset_time"));
        if (now.getHour() == reset.getHour() && now.getMinute() == reset.getMinute() && now.getSecond() == 0) {
            BlockQuota.getInstance().getLogger().info("Reset des quotas à " + now);
            BlockQuota.getInstance().getSqLiteStorage().resetAllQuotas();
        }
    }
}
