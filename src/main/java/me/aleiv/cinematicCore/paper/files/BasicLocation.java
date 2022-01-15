package me.aleiv.cinematicCore.paper.files;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BasicLocation {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final String worldName;

    public BasicLocation(double x, double y, double z, float yaw, float pitch, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
    }

    public BasicLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.worldName = location.getWorld().getName();
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public String getWorldName() {
        return worldName;
    }

    @Override
    public String toString() {
        return "BasicLocation{" + "x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + ", worldName=" + worldName + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasicLocation other = (BasicLocation) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        if (Float.floatToIntBits(this.yaw) != Float.floatToIntBits(other.yaw)) {
            return false;
        }
        if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
            return false;
        }
        if ((this.worldName == null) ? (other.worldName != null) : !this.worldName.equals(other.worldName)) {
            return false;
        }
        return true;
    }

    @Override
    public BasicLocation clone() {
        return new BasicLocation(x, y, z, yaw, pitch, worldName);
    }

}
