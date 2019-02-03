//Ben Nemoy
package dungeon_generator;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Dungeon_generator {
    
    public static void main(String[] args) {
        Frame f = new Frame();
        f.setSize(320, 240);
        f.setVisible(true);
        
//        spore_dungeon spore_map = new spore_dungeon(100, 100);
//        spore_map.generate();
////        spore_map.paint();
////        
////        System.out.println();
////
//        stack_maze stack_map = new stack_maze(15, 15);
//        stack_map.generate();
////        stack_map.paint();
////
////        System.out.println();
////
//        
//        recurse_maze recurse_map = new recurse_maze(9, 9);
//        recurse_map.generate();
////        recurse_map.paint();
//        
//        simple_recurse_maze srecurse_map = new simple_recurse_maze(9, 9);
//        srecurse_map.generate();
//        srecurse_map.paint();
    }
}

class tools {
    public static int middle(int min, int max) {
        return ((max - min) / 2) + min;
    }
    public static int rng(int min, int max) {
        try{
            return new Random().nextInt(max - min) + min;
        } catch(IllegalArgumentException e) {
            return 0;
        }
    }
    public static int cardinality(int bit) {
        int count = 0;
        while(bit > 0) {
            count += bit & 1;
            bit = bit>>1;
        }
        return count;
    }
    public static void timer() {
        long stime, etime;
        stime = System.nanoTime();
        etime = System.nanoTime();
        System.out.println("time: "+(etime-stime));
    }
}

class screen {
    private final tile[] canvas;
    private final int width, height;
    screen(int width, int height) {
        canvas = new tile[width * height];
        this.width = width;
        this.height = height;
        Arrays.fill(canvas, tile.SPACE);
    }
    public void fill(tile type) {
        Arrays.fill(canvas, type);
    }
    public void draw_square(int x0, int y0, int x1, int y1, tile type) {
        for(int i = x0; i<=x1; i++) {
            set_tile(i, y0, type);
            set_tile(i, y1, type);
        }
        for(int i = y0; i<=y1; i++) {
            set_tile(x0, i, type);
            set_tile(x1, i, type);
        }
    }
    public void draw_line(int x0, int y0, int x1, int y1, tile type) {
        int x, y, dx, dy, dx1, dy1, px, py, xe, ye;
            dx = x1 - x0; dy = y1 - y0;
            dx1 = Math.abs(dx); dy1 = Math.abs(dy);
            px = 2 * dy1 - dx1; py = 2 * dx1 - dy1;
            if(dy1 <= dx1) {
                if(dx >= 0) {
                    x = x0; y = y0; xe = x1;
                } else {
                    x = x1; y = y1; xe = x0;
                }
                set_tile(x, y, type);
                
                for(;x<xe;x++) {
                    if(px<0) {
                        px += 2 * dy1;
                    } else {
                        if((dx<0 && dy<0) || (dx>0 && dy>0)) {
                            y++;
                        } else {
                            y--;
                        }
                        px += 2 * (dy1 - dx1);
                    }
                    set_tile(x, y, type);
                }
            } else {
                if(dy >= 0) {
                    x = x0; y = y0; ye = y1;
                } else {
                    x = x1; y = y1; ye = y0;
                }
                set_tile(x, y, type);
                
                for(;y<ye;y++) {
                    if(py<=0) {
                        py += 2 * dx1;
                    } else {
                        if((dx<0 && dy<0) || (dx>0 && dy>0)) {
                            x++;
                        } else {
                            x--;
                        }
                        py += 2 * (dx1 - dy1);
                    }
                    set_tile(x, y, type);
                }
            }
    }
    public void set_tile(int x, int y, tile type) {
        canvas[(y*width)+x] = type;
    }
    public tile get_tile(int x, int y) {
        return canvas[(y*width)+x];
    }
    public void paint() {
        String out = "";
            for (int i=0; i<canvas.length; i++) {
                if(i%width==0) out += "\n";
                switch (canvas[i]) {
                    case SPACE: out += "  "; break;
                    case WALL: out += "##"; break;
                    case WALL2: out += "HH"; break;
                    case TEMP: out += ".."; break;
                    default: out += canvas[i] + " ";
                }
            }
        System.out.println(out);
    }
    enum tile {
        SPACE, WALL, WALL2, TEMP;
    }
}
