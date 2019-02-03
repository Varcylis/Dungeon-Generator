//Ben Nemoy
package dungeon_generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class recurse_maze {
    private final int room_width;
    private final int room_height;
    private int row_doors = 2;
    private int col_doors = 1;
    private ArrayList<point> points = new ArrayList();
    func plus = (x) -> {return (x+1)/2;};
    func minus = (x) -> {return (x-1)/2;};
    func inverse = (x) -> {return (x+1)*2;};
    
    public recurse_maze() {
        this.room_width = 7;
        this.room_height = 7;
    }
    public recurse_maze(int width, int height) {
        this.room_width = width>0?width:2;
        this.room_height = height>0?height:2;
    }
    public void door_frequency(int rows, int columns) {
        this.row_doors = rows;
        this.col_doors = columns;
    }
    
    public void generate() {
        submaze(1, 1, (room_width*2)-1, (room_height*2)-1);
    }
    private void submaze(int x0, int y0, int x1, int y1) {
        point p = new point(rng(x0, x1, minus), rng(y0, y1, minus));
        p.bound(x0, y0, x1, y1);
        p.xspace(spaces(p.x, x0, x1, row_doors));
        p.yspace(spaces(p.y, y0, y1, col_doors));
        points.add(p);
        if(p.x-x0>1 && p.y-y0>1) {submaze(x0, y0, p.x-1, p.y-1);}
        if(p.x-x0>1 && y1-p.y>1) {submaze(x0, p.y+1, p.x-1, y1);}
        if(x1-p.x>1 && y1-p.y>1) {submaze(p.x+1, p.y+1, x1, y1);}
        if(x1-p.x>1 && p.y-y0>1) {submaze(p.x+1, y0, x1, p.y-1);}
    }
    private int[] spaces(int z, int z0, int z1, int num) {
        int[] spaces = new int[num];
        int i = 0;
        if(num%2==1) {
            spaces[i++] = rng(z0, z1, plus)-1;
        }
        for(; i<num; i++) {
           spaces[i] = rng(z0, z-1, plus)-1;
           spaces[++i] = rng(z+1, z1, plus)-1;
        }
        return spaces;
    }
    private int rng(int min, int max, func mod) {
        return inverse.f(tools.rng(minus.f(min), mod.f(max)));
    }
    
    public void paint() {
        int width = (room_width*2);
        int height = (room_height*2);
        screen grid = new screen(width+1, height+1);
        grid.draw_square(0, 0, width, height, screen.tile.WALL);
        points.forEach((p) -> {
            for(int i=p.x0; i<=p.x1; i++) {
                grid.set_tile(i, p.y, screen.tile.WALL);
            }
            for(int i=p.y0; i<=p.y1; i++) {
                grid.set_tile(p.x, i, screen.tile.WALL);
            }
            for(int x: p.xs) {
                grid.set_tile(x, p.y, screen.tile.SPACE);
            }
            for(int y: p.ys) {
                grid.set_tile(p.x, y, screen.tile.SPACE);
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

class point{
    int x, y;
    int x0, y0, x1, y1;
    int[] xs, ys;
    point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    void bound(int x0, int y0, int x1, int y1) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }
    void xspace(int... x) {
        xs = Arrays.copyOf(x, x.length);
    }
    void yspace(int... y) {
        ys = Arrays.copyOf(y, y.length);
    }
    void debug() {
        System.out.println("X: "+x+" |Y: "+y+" |xs: "+Arrays.toString(xs)+" |ys: "+Arrays.toString(ys));
    }
}

interface func {
    int f(int x);
}
