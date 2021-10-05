package me.aleiv.core.paper.utilities.TCT;

import java.util.concurrent.CompletableFuture;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * BukkitTCT - Quick and dirty way to run tasks asynchronously and synchronously
 * without needing any dependencies.
 * 
 * @author jcedeno
 */
public class BukkitTCT extends TaskChainTool {

    private static Plugin plugin = null;

    /**
     * Static method to pass a Bukkit instance to the TaskChainTool. Must be called
     * only once, but can be called as many times as needed in case plugin becomes
     * null.
     * 
     * @param instance Plugin instance
     * @return true if plugin instance was set, false otherwise
     */
    public static boolean registerPlugin(Plugin instance) {
        plugin = instance;
        return isPluginReady();
    }

    /**
     * Helper method to check if plugin instance is ready.
     * 
     * @return true if plugin instance is ready, false otherwise
     */
    public static boolean isPluginReady() {
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public CompletableFuture<Boolean> execute() {
        return CompletableFuture.supplyAsync(() -> {
            /** Loop to execute all tasks in order. */
            while (!isEmpty()) {
                var nextRunnable = poll();
                if (nextRunnable instanceof BukkitRunnable bukkitRunnable)
                    handleBukkit(bukkitRunnable);
                else
                    handleRunnable(nextRunnable); // use the method already defined in TaskChainTool.

            }
            return true;
        });
    }

    /**
     * Helper method to handle BukkitRunnable. Just did this to keep the complexity
     * of {@link #execute()} down.
     * 
     * @param bukkitRunnable
     */
    private void handleBukkit(final BukkitRunnable bukkitRunnable) {
        var bukkitTask = bukkitRunnable.runTask(plugin);
        var id = bukkitTask.getTaskId();

        while (!bukkitTask.isCancelled() && plugin.getServer().getScheduler().isCurrentlyRunning(id)) {
            // Hold on bukkit finishing the task.
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}