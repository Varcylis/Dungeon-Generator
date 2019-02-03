//Ben Nemoy
package dungeon_generator;

import java.util.ArrayList;
import java.util.Arrays;
/*
    This algorithm first populates the map with randomly
    sized rooms and adds each room to a list as they're created.
    Next it linearly goes through the list and finds a common area between each room.
    This area then gets processed for obstructions by other rooms.
    Once the proccess is done and space is available, the hallway is created and added to the list.

    further notes: The list is only looped through for the rooms and not the hallways.

    further ideas: Add curving or right angled hallways. Add branching hallways.
        Add a menu to change values at runtime. Add obstacles and an AI to navigate said obstacles.
*/
public class spore_dungeon {
    private final box_coordinates boundary = new box_coordinates();
    private int box_size_upper_limit = 15;
    private int box_size_lower_limit = 10;
    private final ArrayList<box_coordinates> box_list = new ArrayList(0);
    //box_type 1 is room, box_type 2 is verticle hallway, box_type 3 is horizontal hallway
    private boolean new_box_with_rng() {
        boolean regenerate;
        int rng_limit = 0;
        box_coordinates temp = new box_coordinates();
        temp.box_type = 1;
        temp.SizeX = tools.rng(box_size_lower_limit, box_size_upper_limit);
        temp.SizeY = tools.rng(box_size_lower_limit, box_size_upper_limit);
        do {
            regenerate = false;
            temp.PosX = tools.rng(boundary.west_face(), boundary.east_face() - temp.SizeX);
            temp.PosY = tools.rng(boundary.north_face(), boundary.south_face() - temp.SizeY);
            
            for(box_coordinates box: box_list) {
                if(regenerate = box_coordinates.boxes_overlap(temp, box)) {break;}
            }
            if(rng_limit++ == 8) {return false;}
        } while(regenerate);
        box_list.add(temp);
        return true;
    }
    private box_coordinates find_area_between_boxes(box_coordinates box1, box_coordinates box2) {
        box_coordinates box_space = new box_coordinates();
        //if boxes are vertical from each other
        if(box_coordinates.boxes_overlap_Xplane(box1, box2)) {
            box_space.box_type = 2; //type 2 is vertical hallway
            //if one box is bigger than the other
            if(box_coordinates.box1_encapsulates_box2_Xplane(box1, box2)) {
                box_space.west_face(box2.west_face());
                box_space.east_face(box2.east_face());
            } else if(box_coordinates.box1_encapsulates_box2_Xplane(box2, box1)) {
                box_space.west_face(box1.west_face());
                box_space.east_face(box1.east_face());
            } else {
                if(box_coordinates.box1_offsets_box2_Xplane(box1, box2) < 0) {
                    box_space.west_face(box2.west_face());
                    box_space.east_face(box1.east_face());
                } else {
                    box_space.west_face(box1.west_face());
                    box_space.east_face(box2.east_face());
                }
            }
            
            if(box_coordinates.box1_offsets_box2_Yplane(box1, box2) < 0) {
                box_space.north_face(box1.south_face());
                box_space.south_face(box2.north_face());
            } else {
                box_space.north_face(box2.south_face());
                box_space.south_face(box1.north_face());
            }
        //if boxes are horizontal from each other    
        } else if(box_coordinates.boxes_overlap_Yplane(box1, box2)) {
            box_space.box_type = 3; //type 3 is horizontal hallway
            //if one box is bigger than the other
            if(box_coordinates.box1_encapsulates_box2_Yplane(box1, box2)) {
                box_space.north_face(box2.north_face());
                box_space.south_face(box2.south_face());
            } else if(box_coordinates.box1_encapsulates_box2_Yplane(box2, box1)) {
                box_space.north_face(box1.north_face());
                box_space.south_face(box1.south_face());
            } else {
                if(box_coordinates.box1_offsets_box2_Yplane(box1, box2) < 0) {
                    box_space.north_face(box2.north_face());
                    box_space.south_face(box1.south_face());
                } else {
                    box_space.north_face(box1.north_face());
                    box_space.south_face(box2.south_face());
                }
            }
            
            if(box_coordinates.box1_offsets_box2_Xplane(box1, box2) < 0) {
                box_space.west_face(box1.east_face());
                box_space.east_face(box2.west_face());
            } else {
                box_space.west_face(box2.east_face());
                box_space.east_face(box1.west_face());
            }
        }
        
        return box_space;
    }
    private box_coordinates parse_for_hallway_space(box_coordinates box_space) {
        for(box_coordinates box: box_list) {
            if(box_coordinates.boxes_overlap(box_space, box)) {
                boolean encapsulate_Xplane = box_coordinates.box1_encapsulates_box2_Xplane(box_space, box);
                boolean encapsulate_Yplane = box_coordinates.box1_encapsulates_box2_Yplane(box_space, box);
                int offset_Xplane = box_coordinates.box1_offsets_box2_Xplane(box_space, box);
                int offset_Yplane = box_coordinates.box1_offsets_box2_Yplane(box_space, box);
                if(encapsulate_Xplane && encapsulate_Yplane) {
                    box_space.SizeX = box_space.SizeY = 0;
                } else {
                    if(encapsulate_Xplane) {
                        if(offset_Yplane > 0) {
                            box_space.north_face(box.south_face());
                        } else if(offset_Yplane < 0){
                            box_space.south_face(box.north_face());
                        } else {
                            box_space.SizeY = 0;
                            return box_space;
                        }
                    } else if(encapsulate_Yplane) {
                        if(offset_Xplane > 0) {
                            box_space.west_face(box.east_face());
                        } else if(offset_Xplane < 0) {
                            box_space.east_face(box.west_face());
                        } else {
                            box_space.SizeX = 0;
                            return box_space;
                        }
                    }
                }
            }
        }
        
        if(box_space.box_type == 2 && box_space.SizeX >= 5) {
            int middle = box_space.middle_Xplane();
            box_space.west_face(middle - 2);
            box_space.east_face(middle + 3);
        } else if(box_space.box_type == 3 && box_space.SizeY >= 5) {
            int middle = box_space.middle_Yplane();
            box_space.north_face(middle - 2);
            box_space.south_face(middle + 3);
        } else {
            box_space.SizeX = box_space.SizeY = 0;
        }
        return box_space;
    }
    private boolean add_hallway(box_coordinates hallway) {
        if(hallway.SizeX == 0 || hallway.SizeY == 0) {
            return false;
        }
        box_list.add(hallway);
        return true;
    }
    private void generate_hallways() {
        int list_size = box_list.size();
        for(int i = 0; i < list_size; i++) {
            for(int j = i+1; j < list_size; j++) {
                add_hallway(
                    parse_for_hallway_space(
                        find_area_between_boxes(
                            box_list.get(i), box_list.get(j)
                )));
            }
        }
    }
    public void generate() {
        while(new_box_with_rng()) {}
        generate_hallways();
        //algorithm_test();
    }
    
    spore_dungeon(int map_width, int map_height) {
        boundary.update_size(map_width, map_height);
        
    }
    public void room_size(int room_max_size, int room_min_size) {
        box_size_upper_limit = Math.max(room_min_size, room_max_size);
        box_size_lower_limit = Math.min(room_min_size, room_max_size);
    }
    
    public void debug_print() {
        box_list.forEach((box) -> {
            box.debug_print();
        });
    }
    public void paint() {
        screen grid = new screen(boundary.SizeX, boundary.SizeY);
        int i;
        for(box_coordinates box: box_list) {
            switch (box.box_type) {
                case 1:
                    for(i=box.north_face();i<box.south_face();i++){
                        grid.set_tile(box.west_face(), i, screen.tile.WALL);
                        grid.set_tile(box.east_face()-1, i, screen.tile.WALL);
                    }
                    for(i=box.west_face(); i<box.east_face();i++) {
                        grid.set_tile(i, box.north_face(), screen.tile.WALL);
                        grid.set_tile(i, box.south_face()-1, screen.tile.WALL);
                    }
                    break;
                case 2:
                    for(i=box.north_face();i<box.south_face();i++){
                        grid.set_tile(box.west_face(), i, screen.tile.WALL2);
                        grid.set_tile(box.east_face()-1, i, screen.tile.WALL2);
                    }
                    for(i=box.west_face()+1; i<box.east_face()-1;i++) {
                        grid.set_tile(i, box.north_face()-1, screen.tile.WALL);
                        grid.set_tile(i, box.south_face(), screen.tile.WALL);
                    }
                    break;
                case 3:
                    for(i=box.west_face(); i<box.east_face();i++) {
                        grid.set_tile(i, box.north_face(), screen.tile.WALL2);
                        grid.set_tile(i, box.south_face()-1, screen.tile.WALL2);
                    }
                    for(i=box.north_face()+1;i<box.south_face()-1;i++){
                        grid.set_tile(box.west_face()-1, i, screen.tile.WALL);
                        grid.set_tile(box.east_face(), i, screen.tile.WALL);
                    }
                    break;
            }
            
        }
        grid.paint();
    }
    
    private void sort_box_list() {
        box_list.sort((box_coordinates box1, box_coordinates box2) -> {
            if(box1.PosX == box2.PosX) {
                if(box1.PosY == box2.PosY) {
                    if(box1.SizeX == box2.SizeX) {
                        return box1.SizeY - box2.SizeY;
                    }
                    return box1.SizeX - box2.SizeX;
                }
                return box1.PosY - box2.PosY;
            }
            return box1.PosX - box2.PosX;
        });
    }
    private void sort_box_list_Xplane() {
        box_list.sort((box_coordinates box1, box_coordinates box2) -> {
            return box1.PosX - box2.PosX;
        });
    }
    private void sort_box_list_Yplane() {
        box_list.sort((box_coordinates box1, box_coordinates box2) -> {
            return box1.PosY - box2.PosY;
        });
    }
    
    private void algorithm_test() {
        box_list.add(new box_coordinates());
        box_list.add(new box_coordinates());
        box_list.add(new box_coordinates());
        box_list.get(0).update(2, 2, 10, 10); box_list.get(0).box_type = 1;
        box_list.get(1).update(2, 22, 10, 10); box_list.get(1).box_type = 1;
        box_list.get(2).update(22, 2, 10, 10); box_list.get(2).box_type = 1;
        
        box_coordinates hallway0 = find_area_between_boxes(box_list.get(0), box_list.get(1));
        box_coordinates hallway1 = find_area_between_boxes(box_list.get(0), box_list.get(2));
        hallway0 = parse_for_hallway_space(hallway0);
        hallway1 = parse_for_hallway_space(hallway1);
        add_hallway(hallway0);
        add_hallway(hallway1);
    }
}

class box_coordinates {
    int SizeX = 0, SizeY = 0;
    int PosX = 0, PosY = 0;
    int box_type = 0;
    
    public void update(int posX, int posY, int sizeX, int sizeY) {
        this.update_pos(posX, posY);
        this.update_size(sizeX, sizeY);
    }
    public void update_pos(int x, int y) {
        PosX = x;
        PosY = y;
    }
    public void update_size(int x, int y) {
        SizeX = x;
        SizeY = y;
    }
    
    public int north_face() {
        return this.PosY;
    }
    public int south_face() {
        return PosY + SizeY;
    }
    public int west_face() {
        return this.PosX;
    }
    public int east_face() {
        return PosX + SizeX;
    }
    
    public void north_face(int n) {
        int temp = this.south_face();
        this.PosY = n;
        box_coordinates.this.south_face(temp);
    }
    public void south_face(int s) {
        this.SizeY = s - this.PosY;
    }
    public void west_face(int w) {
        int temp = box_coordinates.this.east_face();
        this.PosX = w;
        box_coordinates.this.east_face(temp);
    }
    public void east_face(int e) {
        this.SizeX = e - this.PosX;
    }
    
    public int middle_Xplane() {
        return tools.middle(this.west_face(), this.east_face());
    }
    public int middle_Yplane() {
        return tools.middle(this.north_face(), this.south_face());
    }
    
    
    public static boolean boxes_collide(box_coordinates box1, box_coordinates box2) {
            return (boxes_collide_Xplane(box1, box2) &&
                   boxes_overlap_Yplane(box1, box2)) ||
                   (boxes_collide_Yplane(box1, box2) &&
                   boxes_overlap_Xplane(box1, box2));
    }
    public static boolean boxes_overlap(box_coordinates box1, box_coordinates box2) {
            return boxes_overlap_Xplane(box1, box2) &&
                   boxes_overlap_Yplane(box1, box2);
    }
    public static boolean box1_encapsulates_box2(box_coordinates box1, box_coordinates box2) {
            return box1_encapsulates_box2_Xplane(box1, box2) &&
                   box1_encapsulates_box2_Yplane(box1, box2);
    }
    public static int box_encapsulates_box2(box_coordinates box1, box_coordinates box2) {
        return box_encapsulates_box2_Xplane(box1, box2) & box_encapsulates_box2_Yplane(box1, box2);
    }
    
    //dont know if this is practically different from boxes_collide() but I'm keeping it
    public static boolean box_collision(box_coordinates box1, box_coordinates box2) {
        return ((boxes_collide_Xplane(box1, box2)) ||
               boxes_collide_Yplane(box1, box2)) &&
               boxes_overlap(box1, box2);
    }
    
    //X plane box boolean tests
    public static boolean boxes_collide_Xplane(box_coordinates box1, box_coordinates box2) {
        return box1.west_face() <= box2.west_face() &&
               box1.east_face() >= box2.west_face() ||
               box1.west_face() <= box2.east_face() && 
               box1.east_face() >= box2.east_face();
    }
    public static boolean boxes_overlap_Xplane(box_coordinates box1, box_coordinates box2) {
        return box1.west_face() <= box2.east_face() &&
               box1.east_face() >= box2.west_face();
    }
    public static boolean box1_encapsulates_box2_Xplane(box_coordinates box1, box_coordinates box2) {
        return box1.west_face() <= box2.west_face() &&
               box1.east_face() >= box2.east_face();
    }
    
    //Y plane box boolean tests
    public static boolean boxes_collide_Yplane(box_coordinates box1, box_coordinates box2) {
        return box1.north_face() <= box2.north_face() &&
               box1.south_face() >= box2.north_face() ||
               box1.north_face() <= box2.south_face() &&
               box1.south_face() >= box2.south_face();
    }
    public static boolean boxes_overlap_Yplane(box_coordinates box1, box_coordinates box2) {
        return box1.north_face() <= box2.south_face() &&
               box1.south_face() >= box2.north_face();
    }
    public static boolean box1_encapsulates_box2_Yplane(box_coordinates box1, box_coordinates box2) {
        return box1.north_face() <= box2.north_face() &&
               box1.south_face() >= box2.south_face();
    }

    //returns positive if offsets east, negative if offsets west, 0 if no offset
    public static int box1_offsets_box2_Xplane(box_coordinates box1, box_coordinates box2) {
            return box1.middle_Xplane() - box2.middle_Xplane();
        }
    //returns positive if offsets south, negative if offsets north, 0 if no offset
    public static int box1_offsets_box2_Yplane(box_coordinates box1, box_coordinates box2) {
            return box1.middle_Yplane() - box2.middle_Yplane();
        }
    //returns positive if box1 encapsulates box2, negative if box2 encapsulates box1, 0 if no encapsulation
    public static int box_encapsulates_box2_Xplane(box_coordinates box1, box_coordinates box2) {
        if(boxes_collide_Xplane(box1, box2)) {
            return 0;
        }
        return (box2.west_face() - box1.west_face()) + (box1.east_face() - box2.east_face());
    }
    //returns positive if box1 encapsulates box2, negative if box2 encapsulates box1, 0 if no encapsulation
    public static int box_encapsulates_box2_Yplane(box_coordinates box1, box_coordinates box2) {
        if(boxes_collide_Yplane(box1, box2)) {
            return 0;
        }
        return (box2.north_face() - box1.north_face()) + (box1.south_face() - box2.south_face());
    }
    
    //returns positive if box1 is bigger than box2, negative if box2 is bigger than box1, 0 if same
    public static int box1_bigger_than_box2_Xplane(box_coordinates box1, box_coordinates box2) {
        return box1.SizeX - box2.SizeX;
    }
    public static int box1_bigger_than_box2_Yplane(box_coordinates box1, box_coordinates box2) {
        return box1.SizeY - box2.SizeY;
    }
    
    //finds center space coordinates between boxes    
    public static int center_betwen_boxes_Xplane(box_coordinates box1, box_coordinates box2) {
        return tools.middle(box1.middle_Xplane(), box2.middle_Xplane());
    }
    public static int center_between_boxes_Yplane(box_coordinates box1, box_coordinates box2) {
        return tools.middle(box1.middle_Yplane(), box2.middle_Yplane());
    }
    
    
    public void debug_print() {
        System.out.println("x0: "+PosX+", y0: "+PosY+", x1: "+SizeX+", y1: "+SizeY);
    }
}
