//Ben Nemoy
package dungeon_generator;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class stack_maze { 
    private final int room_width;
    private final int room_height;
    private int start_pos_x = 0;
    private int start_pos_y = 0;
    private final Stack<position> stack = new Stack();
    private final ArrayList<position> list = new ArrayList(0);
    
    public stack_maze() {
        this.room_width = 7;
        this.room_height = 7;
    }
    public stack_maze(int width, int height) {
        this.room_width = width>0?width:1;
        this.room_height = height>0?height:1;
    }
    public void starting_pos(int x, int y) {
        start_pos_x = x;
        start_pos_y = y;
    }
    
    public void generate() {
        stack.push(new position(start_pos_x, start_pos_y, Flags.NUL));
        list.add(stack.peek());
        while(!stack.empty()) {
            switch(stack.peek().rng()) {
                case N:
                    stack.peek().set_flag(Flags.N);
                    stack.push(new position(stack.peek().x, stack.peek().y-1, Flags.S));
                    break; //up
                case S:
                    stack.peek().set_flag(Flags.S);
                    stack.push(new position(stack.peek().x, stack.peek().y+1, Flags.N));
                    break; //down
                case W:
                    stack.peek().set_flag(Flags.W);
                    stack.push(new position(stack.peek().x-1, stack.peek().y, Flags.E));
                    break; //left
                case E:
                    stack.peek().set_flag(Flags.E);
                    stack.push(new position(stack.peek().x+1, stack.peek().y, Flags.W));
                    break; //right
                default:
                    stack.pop();
                    continue;
            }
            if(pushable()) {
                list.add(stack.peek());
            } else {
                stack.pop();
            }
        }
    }
    private boolean pushable() {
        return (stack.peek().x >= 0 && stack.peek().x < room_width) &&
               (stack.peek().y >= 0 && stack.peek().y < room_height)&&
               !list_contains(stack.peek());
    }
    private boolean list_contains(position pos) {
        return list.stream().anyMatch((obj) -> (pos.x == obj.x && pos.y == obj.y));
    }
    
    public void paint() {
        int x, y;
        screen grid = new screen((room_width*2)+1, (room_height*2)+1);
        grid.fill(screen.tile.WALL);
        for(position pos: list) {
            x = (pos.x+pos.x)+1;
            y = (pos.y+pos.y)+1;
            grid.set_tile(x, y, screen.tile.SPACE);
            switch(pos.path) {
                case N: grid.set_tile(x, y-1, screen.tile.SPACE); break;
                case S: grid.set_tile(x, y+1, screen.tile.SPACE); break;
                case W: grid.set_tile(x-1, y, screen.tile.SPACE); break;
                case E: grid.set_tile(x+1, y, screen.tile.SPACE); break;
            }
        }
        grid.paint();
    }
    void sort_list() {
        list.sort((position pos1, position pos2) -> {
            if(pos1.y == pos2.y) {
                return pos1.x - pos2.x;
            }
            return pos1.y - pos2.y;
        });
    }
    void debug_list() {
        list.forEach((space) -> {
            System.out.print(list.indexOf(space)+": ");
            space.debug();
        });
    }
    void flag_test() {
        position test = new position(0, 0, Flags.NUL);
        Flags result;
        test.set_flag(result = test.rng()); System.out.print(result+" ");
        test.set_flag(result = test.rng()); System.out.print(result+" ");
        test.set_flag(result = test.rng()); System.out.print(result+" ");
        test.set_flag(result = test.rng()); System.out.print(result+" ");
        System.out.println();
    }
}

class position {
    int x, y;
    Flags path;
    private int flags = 0b0000;
    position(int x, int y, Flags flag) {
        this.x = x;
        this.y = y;
        this.path = flag;
        flags = flag.value();
    }
    void set_flag(Flags flag) {
        flags |= flag.value();
    }
    void reset_flags(Flags flag) {
        flags = flag.value();
    }
    boolean flag_state(Flags flag) {
        return (this.flags & flag.value()) == flag.value();
    }
    Flags rng() {
        int i = 0;
        Flags direction[] = new Flags[4 - tools.cardinality(flags)];
        if(direction.length <= 0) {
            return Flags.NUL;
        }
        if(!flag_state(Flags.N)) {direction[i] = Flags.N; i++;}
        if(!flag_state(Flags.S)) {direction[i] = Flags.S; i++;}
        if(!flag_state(Flags.W)) {direction[i] = Flags.W; i++;}
        if(!flag_state(Flags.E)) {direction[i] = Flags.E; i++;}
        i = direction.length == 1? 0 :new Random().nextInt(i);
        return direction[i];
    }
    
    void debug() {
        System.out.println("x: "+x+" |y: "+y+" |flags: "+path);
    }
}

enum Flags {
    NUL(0b0000), N(0b0001), S(0b0010), W(0b0100), E(0b1000), ALL(0b1111);
    private final int flag;
    Flags(int value) {
        this.flag = value;
    }
    int value() {
        return flag;
    }
}
