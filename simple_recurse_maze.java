//Ben Nemoy
package dungeon_generator;

import java.util.ArrayList;

public class simple_recurse_maze {
    private final int room_width;
    private final int room_height;
    private ArrayList<wall> points = new ArrayList();
    func plus = (x) -> {return (x+1)/2;};
    func minus = (x) -> {return (x-1)/2;};
    func inverse = (x) -> {return (x+1)*2;};
    
    public simple_recurse_maze() {
        this.room_width = 7;
        this.room_height = 7;
    }
    public simple_recurse_maze(int width, int height) {
        this.room_width = width>0?width:2;
        this.room_height = height>0?height:2;
    }
    
    public void generate() {
        divide(1, (room_width*2)-1, 1, (room_height*2)-1, true);
    }
    private void divide(int wallmin, int wallmax, int doormin, int doormax, boolean orientation) {
        wall p = new wall(rng(wallmin, wallmax, minus), doormin, doormax, orientation);
        p.door(rng(doormin, doormax, plus)-1);
        points.add(p);
        if(p.wall-wallmin>1) {divide(doormin, doormax, wallmin, p.wall-1, !orientation);}
        if(wallmax-p.wall>1) {divide(doormin, doormax, p.wall+1, wallmax, !orientation);}
    }
    private int rng(int min, int max, func mod) {
        return inverse.f(tools.rng(minus.f(min), mod.f(max)));
    }
    
    public void paint() {
        int width = (room_width*2), height = (room_height*2);
        screen grid = new screen(width+1, height+1);
        grid.draw_square(0, 0, width, height, screen.tile.WALL);
        points.forEach((p) -> {
            if(p.orientation) {
                for(int i=p.min; i<=p.max; i++) {
                    grid.set_tile(p.wall, i, screen.tile.WALL);
                }
                grid.set_tile(p.wall, p.door, screen.tile.SPACE);
            } else {
                for(int i=p.min; i<=p.max; i++) {
                    grid.set_tile(i, p.wall, screen.tile.WALL);
                }
                grid.set_tile(p.door, p.wall, screen.tile.SPACE);
            }
        });
        grid.paint();
    }
    public void debug() {
        points.forEach((point) -> {
            point.debug();
        });
    }
}

class wall{
    int wall, door, min, max;
    boolean orientation;
    wall(int wall, int min, int max, boolean o) {
        this.wall = wall;
        this.min = min;
        this.max = max;
        this.orientation = o;
    }
    void door(int door) {
        this.door = door;
    }
    void debug() {
        System.out.println("wall: "+wall+" |door: "+door+
                " |min: "+min+" |max: "+max+" |O: "+orientation);
    }
}

interface simple_func {
    int f(int x);
}
