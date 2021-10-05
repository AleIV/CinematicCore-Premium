package me.aleiv.core.paper.objects;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Cinematic {
    
    List<Frame> frames;
    String name;

    public Cinematic(String name){
        this.name = name;
        this.frames = new ArrayList<>();

    }

    public Cinematic(String name, List<Frame> frames){
        this.name = name;
        this.frames = frames;

    }

    /**
     * Obtain the delta of two frames and return them back.
     * 
     * @param one The first frame.
     * @param two The second frame.
     * @return The delta of the two frames smoothed out.
     */
    
    private Frame combineFrames(Frame one, Frame two) {
        return new Frame(one.getWorld(), (one.getX() + two.getX()) / 2, (one.getY() + two.getY()) / 2,
                (one.getZ() + two.getZ()) / 2, (one.getYaw() + two.getYaw()) / 2,
                (one.getPitch() + two.getPitch()) / 2);
    }

    /**
     * @return A list containing half the frames of the cinematic.
     */
    public List<Frame> getSmoothedFrames() {
        var iter = frames.iterator();
        var list = new ArrayList<Frame>();

        while (iter.hasNext()) {
            var frame = iter.next();
            list.add(iter.hasNext() ? combineFrames(frame, iter.next()) : frame);
        }
        return list;
    }

     /**
     * @return A list containing half the frames of the cinematic.
     */
    public List<Frame> getProlongedFrames() {
        var iter = frames.iterator();
        var list = new ArrayList<Frame>();

        while (iter.hasNext()) {
            var frame = iter.next();
            // Add current frame
            list.add(frame);
            // Check if there is a next frame
            if (iter.hasNext()) {
                // Obtain the next frame
                var nextFrame = iter.next();
                // Combine the two frames
                var combinedFrame = combineFrames(frame, nextFrame);
                // Add the combined frame and then the next frame.
                list.add(combinedFrame);
                list.add(nextFrame);
            }
        }
        return list;
    }

    


}
