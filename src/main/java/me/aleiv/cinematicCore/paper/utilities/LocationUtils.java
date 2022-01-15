package me.aleiv.cinematicCore.paper.utilities;

import org.bukkit.Location;

public class LocationUtils {

    public static Location getSafeLocation(Location location) {
        Location loc = location.clone();

        for (int i = 0; i < 256; i++) {
            loc.add(0, -1, 0);
            if (loc.getBlock().getType().isSolid()) {
                loc.add(0, 1, 0);
                loc.setY(loc.getBlockY());
                if (loc.getBlock().getType().toString().contains("SLAB") || loc.getBlock().getType().toString().contains("STAIRS")) {
                    loc.add(0, 0.5, 0);
                }
                return loc;
            }
        }

        return location;
    }

}
