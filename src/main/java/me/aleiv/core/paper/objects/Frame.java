package me.aleiv.core.paper.objects;

import lombok.Data;

@Data
public class Frame {
    String world;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;
    
    public Frame(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;

    }

}
