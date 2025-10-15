/*
Name: Jennifer Huang
Starting date: 2022-12-21
Purpose: To allow the user to manage housekeeping services of a hotel
*/
import java.io.*;
import java.util.Scanner;

class Main {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        // ===== variables =====
        int day = 0;
        String programchoice;
        String name = " ";
        int roomamount = 0;
        int roomnum = 0;
        String roomnumber;
        int requestcount = 0, urgentcount = 0, occupiedcount = 0, dirtycount = 0, cleancount = 0, vacantcount = 0;
        char rtype1='E', rtype2='Z', rtype3='R';
        double bill = 0;

        // ===== arrays =====
        String[] occupation1 = new String[10];
        String[] occupation2 = new String[10];
        String[] occupation3 = new String[10];

        int[] occupiedbefore1 = new int[10];
        int[] occupiedbefore2 = new int[10];
        int[] occupiedbefore3 = new int[10];

        int[] hours1 = new int[10];     // check-in hour (persist while occupied)
        int[] hours2 = new int[10];
        int[] hours3 = new int[10];

        int[] outHours1 = new int[10];  // check-out hour (only on checkout day)
        int[] outHours2 = new int[10];
        int[] outHours3 = new int[10];

        int[] clock1 = new int[10]; // -1 none, 1 check-in today, 2 check-out today
        int[] clock2 = new int[10];
        int[] clock3 = new int[10];

        String[] housekeeping1 = new String[10];
        String[] housekeeping2 = new String[10];
        String[] housekeeping3 = new String[10];

        int[] dirtycounter1 = new int[10];
        int[] dirtycounter2 = new int[10];
        int[] dirtycounter3 = new int[10];

        int[] changecounter1 = new int[10]; // cleaned today flags
        int[] changecounter2 = new int[10];
        int[] changecounter3 = new int[10];

        int[] roomworkcountP = new int[30];
        String[] roomnumP = new String[30];

        String[] wnames = {"Sara","Ivan","Emma","Mary","Ruby","Lily","Adam","Owen","Noah","Liam","John","Evan"};
        String[] wnameavailability = new String[12];
        int[] workcount = new int[12];

        String[] room = {"01","02","03","04","05","06","07","08","09","10"};

        instructions();

        // init state
        initialOccupation(occupation1, occupiedbefore1, clock1, hours1, occupiedcount, vacantcount);
        initialOccupation(occupation2, occupiedbefore2, clock2, hours2, occupiedcount, vacantcount);
        initialOccupation(occupation3, occupiedbefore3, clock3, hours3, occupiedcount, vacantcount);

        initialHousekeeping(occupation1, housekeeping1, changecounter1, occupiedbefore1, urgentcount, requestcount, dirtycount, cleancount);
        initialHousekeeping(occupation2, housekeeping2, changecounter2, occupiedbefore2, urgentcount, requestcount, dirtycount, cleancount);
        initialHousekeeping(occupation3, housekeeping3, changecounter3, occupiedbefore3, urgentcount, requestcount, dirtycount, cleancount);

        // Validate initial state before first print
        validateAndRepairAll(
            occupation1, housekeeping1, clock1, hours1, outHours1, changecounter1, occupiedbefore1,
            occupation2, housekeeping2, clock2, hours2, outHours2, changecounter2, occupiedbefore2,
            occupation3, housekeeping3, clock3, hours3, outHours3, changecounter3, occupiedbefore3
        );

        printStatus(occupation1, occupation2, occupation3,
                    housekeeping1, housekeeping2, housekeeping3,
                    room, rtype1, rtype2, rtype3,
                    hours1, hours2, hours3,
                    outHours1, outHours2, outHours3,
                    clock1, clock2, clock3);

        while (day < 7) {
            dirtycounter(dirtycounter1, housekeeping1);
            dirtycounter(dirtycounter2, housekeeping2);
            dirtycounter(dirtycounter3, housekeeping3);
            availabilityWorker(wnameavailability);

            System.out.println("Day " + (day + 1));
            System.out.println("Would you like to proceed with the program? ");
            programchoice = input.next();
            while (!programchoice.equalsIgnoreCase("yes") && !programchoice.equalsIgnoreCase("no")) {
                System.out.println("Please answer yes or no ⚠️");
                System.out.println("Would you like to proceed with the program? ");
                programchoice = input.next();
            }

            if (!programchoice.equalsIgnoreCase("no")) {
                roomsWork(roomworkcountP, roomnumP);
                System.out.println("How many rooms would you like to clean today?");
                roomamount = input.nextInt();
                while (roomamount > 30 || roomamount < 0) {
                    System.out.println("Error, please enter valid room amount❗");
                    System.out.println("How many rooms would you like to clean today? ");
                    roomamount = input.nextInt();
                }

                for (int i = 0; i < roomamount; i++) {
                    System.out.println("What room number would you like to work on? ");
                    roomnum = input.nextInt();
                    while (!((roomnum>=101 && roomnum<=110) || (roomnum>=201 && roomnum<=210) || (roomnum>=301 && roomnum<=310))) {
                        System.out.println("Error, please enter valid room number❗");
                        System.out.println("What room number would you like to work on? ");
                        roomnum = input.nextInt();
                    }

                    // worker selection
                    System.out.println("Which worker would you like to assign to this room? ");
                    printWorker(wnames, wnameavailability);

                    int workerIndex = -1;
                    while (true) {
                        name = input.next();
                        workerIndex = -1;
                        for (int u = 0; u < wnames.length; u++) {
                            if (name.equalsIgnoreCase(wnames[u])) { workerIndex = u; break; }
                        }
                        if (workerIndex == -1) {
                            System.out.println("Error, please enter valid worker name❗");
                            System.out.println("Which worker would you like to assign to this room? ");
                            continue;
                        }
                        if (wnameavailability[workerIndex].equalsIgnoreCase("Unavailable")) {
                            System.out.println("This worker can't work for the day!");
                            System.out.println("Which worker would you like to assign to this room? ");
                            continue;
                        }
                        if (workcount[workerIndex] >= 3) {
                            System.out.println("This worker can't work anymore for the day!");
                            System.out.println("Which worker would you like to assign to this room? ");
                            continue;
                        }
                        workcount[workerIndex]++;
                        System.out.println("They can work on this room!");
                        break;
                    }

                    roomnumber = Integer.toString(roomnum);
                    if (roomnumber.startsWith("1")) {
                        cleanStatus(housekeeping1, changecounter1, cleancount, roomnumber);
                    } else if (roomnumber.startsWith("2")) {
                        cleanStatus(housekeeping2, changecounter2, cleancount, roomnumber);
                    } else if (roomnumber.startsWith("3")) {
                        cleanStatus(housekeeping3, changecounter3, cleancount, roomnumber);
                    }

                    bill = billhousekeep(bill, roomnumber);
                    requestcmessage();
                }
            }

            enddaymessage();
            for (int w = 0; w < 12; w++) workcount[w] = 0;

            // Occupancy engine (attempts check-in/out with your rules)
            statusOccupation(occupation1, housekeeping1, changecounter1, occupiedbefore1, clock1, hours1, outHours1);
            statusOccupation(occupation2, housekeeping2, changecounter2, occupiedbefore2, clock2, hours2, outHours2);
            statusOccupation(occupation3, housekeeping3, changecounter3, occupiedbefore3, clock3, hours3, outHours3);

            // Housekeeping random events
            statusHousekeeping(occupation1, housekeeping1, changecounter1, occupiedbefore1, urgentcount, requestcount, dirtycount);
            statusHousekeeping(occupation2, housekeeping2, changecounter2, occupiedbefore2, urgentcount, requestcount, dirtycount);
            statusHousekeeping(occupation3, housekeeping3, changecounter3, occupiedbefore3, urgentcount, requestcount, dirtycount);

            // >>> VALIDATE & REPAIR overall consistency before printing <<<
            validateAndRepairAll(
                occupation1, housekeeping1, clock1, hours1, outHours1, changecounter1, occupiedbefore1,
                occupation2, housekeeping2, clock2, hours2, outHours2, changecounter2, occupiedbefore2,
                occupation3, housekeeping3, clock3, hours3, outHours3, changecounter3, occupiedbefore3
            );

            printStatus(occupation1, occupation2, occupation3,
                        housekeeping1, housekeeping2, housekeeping3,
                        room, rtype1, rtype2, rtype3,
                        hours1, hours2, hours3,
                        outHours1, outHours2, outHours3,
                        clock1, clock2, clock3);

            for (int c = 0; c < 10; c++) {
                changecounter1[c] = 0;
                changecounter2[c] = 0;
                changecounter3[c] = 0;
            }
            day++;
        }

        endweekmessage();
        statistics(vacantcount, occupiedcount, cleancount, dirtycount, requestcount, urgentcount, bill);
        input.close();
    }

    // ===== Consistency validator (repairs arrays so the text output always makes sense) =====
    private static void validateAndRepairAll(
        String[] o1, String[] h1, int[] c1, int[] in1, int[] out1, int[] ch1, int[] ob1,
        String[] o2, String[] h2, int[] c2, int[] in2, int[] out2, int[] ch2, int[] ob2,
        String[] o3, String[] h3, int[] c3, int[] in3, int[] out3, int[] ch3, int[] ob3
    ){
        validateAndRepairFloor(o1, h1, c1, in1, out1, ch1, ob1);
        validateAndRepairFloor(o2, h2, c2, in2, out2, ch2, ob2);
        validateAndRepairFloor(o3, h3, c3, in3, out3, ch3, ob3);
    }

    private static void validateAndRepairFloor(String[] oStatus, String[] hkStatus, int[] clock, int[] inHours, int[] outHours, int[] cleanedToday, int[] occupiedBefore){
        for (int i=0; i<oStatus.length; i++){
            String o  = oStatus[i] == null ? "Vacant"  : oStatus[i];
            String hk = hkStatus[i]== null ? "Clean"   : hkStatus[i];
            int ch    = (cleanedToday[i]==1) ? 1 : 0;

            // if cleaned today, it's Clean (regardless of previous)
            if (ch == 1) hk = "Clean";

            // Normalize by occupancy
            if ("Occupied".equals(o)) {
                // Dirty rooms may not be occupied -> override HK to Occupied
                if ("Dirty".equals(hk)) hk = "Occupied";
                // "Clean" while occupied doesn't make sense -> set to Occupied
                if ("Clean".equals(hk)) hk = "Occupied";
                // Requested/Urgent can stay (service while occupied)
                // Times: must have an inHour; no checkout time today
                if (inHours[i] <= 0) inHours[i] = rand(8,18);
                clock[i] = (clock[i]==1) ? 1 : -1; // only allow "check-in today" or none
                outHours[i] = 0;
            } else { // Vacant
                // HK "Occupied" or "Requested" while vacant -> needs work
                if ("Occupied".equals(hk)) hk = (ch==1 ? "Clean" : (occupiedBefore[i]>0 ? "Dirty" : "Clean"));
                if ("Requested".equals(hk)) hk = "Dirty";
                // If it's Dirty/Urgent it's fine to be vacant.

                // If there's a check-in flag but status says Vacant, align to Occupied
                if (clock[i] == 1) {
                    o = "Occupied";
                    hk = "Occupied";
                    if (inHours[i] <= 0) inHours[i] = rand(8,18);
                    outHours[i] = 0;
                }

                // If checkout flag present, ensure out > in and mark Dirty (unless cleaned)
                if (clock[i] == 2) {
                    if (inHours[i] <= 0) inHours[i] = rand(8,18);
                    int outH = outHours[i] > 0 ? outHours[i] : inHours[i] + rand(1,3);
                    if (outH <= inHours[i]) outH = Math.min(24, inHours[i] + 1);
                    outHours[i] = clampHour(outH);
                    hk = (ch==1 ? "Clean" : "Dirty");
                }
            }

            // Write back
            oStatus[i]  = o;
            hkStatus[i] = hk;
        }
    }

    // ===== formatting helpers =====
    private static String[] displayTimes(String oStatus, int clockFlag, int inHour, int outHour) {
        String in = "N/A";
        String out = "N/A";
        if ("Occupied".equals(oStatus)) {
            if (clockFlag == 1) in = "At" + clampHour(inHour);
        } else { // Vacant
            if (clockFlag == 2) {
                int inDisp = clampHour(inHour);
                int outDisp = clampHour(outHour);
                if (outDisp <= inDisp) outDisp = Math.min(24, inDisp + 1);
                in  = "At" + inDisp;
                out = "At" + outDisp;
            }
        }
        return new String[]{in, out};
    }

    private static int rand(int lo, int hi) { return lo + (int)(Math.random() * (hi - lo + 1)); }
    private static int clampHour(int h) { return Math.max(1, Math.min(24, h)); }

    // ===== messages =====
    public static void instructions(){
        System.out.println("Welcome to HotelKeeping!");
        System.out.println("Here in this database program, you can easily manage your hotel's housekeeping services for the week!");
        System.out.println("All you have to do is tell us how many rooms you want to clean, which room by putting the room number and choosing which one of your staff members to clean the room!");
    }
    public static void requestcmessage(){
        int rannum = (int)(Math.random()*(5-1+1)+1);
        switch(rannum){
            case 1: System.out.println("Your request will be worked on!!"); break;
            case 2: System.out.println("Lets get this room cleaned!"); break;
            case 3: System.out.println("Cleanliness is in this room's future!"); break;
            case 4: System.out.println("Let's get rid of that dirt!"); break;
            case 5: System.out.println("Cleaning time!"); break;
        }
    }
    public static void enddaymessage(){
        int rannum = (int)(Math.random()*(5-1+1)+1);
        switch(rannum){
            case 1: System.out.println("Woohoo! Today's management is over!"); break;
            case 2: System.out.println("Congrats! You have finished a day of management "); break;
            case 3: System.out.println("Yay! Management is completed for the day!!"); break;
            case 4: System.out.println("Yess!! Management finished today "); break;
            case 5: System.out.println("Who's done management for the day? YOU!!!"); break;
        }
    }
    public static void endweekmessage(){
        int rannum = (int)(Math.random()*(5-1+1)+1);
        switch(rannum){
            case 1: System.out.println("You made it! This week's management is over!"); break;
            case 2: System.out.println("Congrats! The week's over as well as your management! "); break;
            case 3: System.out.println("Yay! Management is completed for the week!!"); break;
            case 4: System.out.println("7 days of management is completed! Congratulations!!! "); break;
            case 5: System.out.println("A weeks worth of management completed!"); break;
        }
    }

    // ===== printing =====
    public static void printStatus (String[] occupation1, String[] occupation2, String[] occupation3,
                                    String[] housekeep1,  String[] housekeep2,  String[] housekeep3,
                                    String[] floor, char E, char Z, char R,
                                    int[] hours1, int[] hours2, int[] hours3,
                                    int[] outHours1, int[] outHours2, int[] outHours3,
                                    int[] clock1, int[] clock2, int[] clock3) throws IOException {
        PrintWriter t1 = new PrintWriter("StatusofHotel.txt");

        java.util.function.IntFunction<Character> typeForIndex = idx -> {
            if (idx <= 4) return R;      // 01..05
            else if (idx <= 7) return Z; // 06..08
            else return E;                // 09..10
        };

        // floor 1
        for (int i=0; i<10; i++){
            char symbol = typeForIndex.apply(i);
            String o  = occupation1[i]  == null ? "Vacant" : occupation1[i];
            String hk = housekeep1[i]   == null ? "Clean"  : housekeep1[i];
            String[] times = displayTimes(o, clock1[i], hours1[i], outHours1[i]);
            t1.println(symbol+"1"+floor[i]+"\t"+"Occupation Status: "+o+" Clock in time: "+times[0]+" Clock out time: "+times[1]+" Housekeeping Status: "+hk);
        }
        // floor 2
        for (int i=0; i<10; i++){
            char symbol = typeForIndex.apply(i);
            String o  = occupation2[i]  == null ? "Vacant" : occupation2[i];
            String hk = housekeep2[i]   == null ? "Clean"  : housekeep2[i];
            String[] times = displayTimes(o, clock2[i], hours2[i], outHours2[i]);
            t1.println(symbol+"2"+floor[i]+"\t"+"Occupation Status: "+o+" Clock in time: "+times[0]+" Clock out time: "+times[1]+" Housekeeping Status: "+hk);
        }
        // floor 3
        for (int i=0; i<10; i++){
            char symbol = typeForIndex.apply(i);
            String o  = occupation3[i]  == null ? "Vacant" : occupation3[i];
            String hk = housekeep3[i]   == null ? "Clean"  : housekeep3[i];
            String[] times = displayTimes(o, clock3[i], hours3[i], outHours3[i]);
            t1.println(symbol+"3"+floor[i]+"\t"+"Occupation Status: "+o+" Clock in time: "+times[0]+" Clock out time: "+times[1]+" Housekeeping Status: "+hk);
        }

        t1.close();
    }

    // ===== occupancy engine (probabilistic) =====
    public static void statusOccupation (String[] oStatus, String[] hStatus, int[] change, int[] occupied, int[] clock, int[] inHours, int[] outHours){
        for (int i=0; i<oStatus.length; i++){
            String o = (oStatus[i]==null) ? "Vacant" : oStatus[i];
            String hk = (hStatus[i]==null) ? "Clean" : hStatus[i];
            boolean isDirtyLike = "Dirty".equals(hk) || "Urgent".equals(hk);

            // default: no event today
            clock[i] = (clock[i]==1 || clock[i]==2) ? clock[i] : -1;
            outHours[i] = 0;

            if ("Occupied".equals(o)) {
                boolean checkoutToday = (rand(1,10) <= 3); // ~30% chance
                if (checkoutToday) {
                    oStatus[i] = "Vacant";
                    clock[i] = 2;
                    int inH = clampHour(inHours[i] > 0 ? inHours[i] : rand(8,18));
                    inHours[i] = inH;
                    int outH = Math.min(24, inH + rand(1,3));
                    outHours[i] = outH;
                    hStatus[i] = "Dirty";
                    change[i] = 0;
                } else {
                    if (inHours[i] <= 0) inHours[i] = rand(8,18);
                    clock[i] = -1;
                }
                occupied[i]++;
            } else { // Vacant
                if (isDirtyLike) {
                    oStatus[i] = "Vacant";
                    clock[i] = -1;
                    continue;
                }
                boolean canCheckIn = "Clean".equals(hk);
                if (!canCheckIn) {
                    if ("Requested".equals(hk)) hStatus[i] = "Dirty";
                    oStatus[i] = "Vacant";
                    clock[i] = -1;
                    continue;
                }
                boolean checkinToday = (rand(1,10) <= 3); // ~30%
                if (checkinToday) {
                    oStatus[i] = "Occupied";
                    clock[i] = 1;
                    inHours[i] = rand(8,18);
                    outHours[i] = 0;
                    hStatus[i] = "Occupied";
                    occupied[i]++;
                } else {
                    oStatus[i] = "Vacant";
                    clock[i] = -1;
                }
            }
        }
    }

    // ===== your original init / housekeeping logic =====
    public static void initialOccupation (String[] oStatus, int[] occupied, int[] clock, int[] time, int oCount, int vCount){
        for (int i=0; i<oStatus.length; i++){
            int b = (int)(Math.random()*10)+1; // 1..10
            if (b==1||b==3||b==5||b==7||b==9){
                oStatus[i]="Occupied";
                int c = (int)(Math.random()*(4-3+1))+3; // 3 or 4
                if (c==3) clock[i]=1; else clock[i]=-1;
                oCount++;
                time[i]=(int)(Math.random()*24)+1;
            } else {
                oStatus[i]="Vacant";
                vCount++;
                clock[i]=-1;
            }
            if (oStatus[i].equals("Occupied")) occupied[i]++;
        }
    }
    public static void initialHousekeeping(String[] oStatus, String[] hStatus, int[] change, int[] occupied,
                                           int uCount, int rCount, int dcount, int ccount){
        for (int i=0; i<hStatus.length; i++){
            if (change[i]==0){
                if (oStatus[i].equals("Occupied")){
                    int b = (int)(Math.random()*24)+1;
                    if (b==1||b==3||b==5){ hStatus[i]="Requested"; rCount++; }
                    else if (b==10)      { hStatus[i]="Urgent";    uCount++; }
                    else                 { hStatus[i]="Occupied"; }
                } else {
                    int c = (int)(Math.random()*2)+1; // 1..2
                    if (c==2){ hStatus[i]="Clean";  ccount++; }
                    else     { hStatus[i]="Dirty";  dcount++; }
                    if (occupied[i]==1){ hStatus[i]="Dirty"; dcount++; }
                }
            }
        }
    }
    public static void statusHousekeeping(String[] oStatus, String[] hStatus, int[] change, int[] occupied,
                                          int uCount, int rCount, int dcount){
        for (int i=0; i<hStatus.length; i++){
            if (change[i]==0){
                if (oStatus[i].equals("Occupied")){
                    int b = (int)(Math.random()*24)+1;
                    if (b==1||b==3||b==5){ hStatus[i]="Requested"; rCount++; }
                    else if (b==10)      { hStatus[i]="Urgent";    uCount++; }
                    else                 { hStatus[i]="Occupied"; }
                } else {
                    if (!"Clean".equals(hStatus[i])) hStatus[i]="Dirty";
                    if (occupied[i]==1){ hStatus[i]="Dirty"; dcount++; }
                }
            }
        }
    }

    // ===== actions =====
    public static void cleanStatus(String[] housekeep, int[] change, int cCount, String roomnum){
        String rnum = roomnum.substring(1); // "101" -> "01"
        int i = Integer.parseInt(rnum) - 1; // 0..9
        if (i>=0 && i<housekeep.length){
            housekeep[i]="Clean";
            change[i]=1;
        }
        cCount++;
    }

    // ===== workers =====
    public static void printWorker(String[] a, String[] b){
        for (int i=0; i<12; i++){
            System.out.println(" \t"+a[i]+" \t"+b[i]);
        }
    }
    public static String[] availabilityWorker(String[] a){
        for (int i=0; i<12; i++){
            int a1 = (int)(Math.random()*7)+1; // 1..7
            if (a1==1||a1==3||a1==5||a1==7) a[i]="Available";
            else a[i]="Unavailable";
        }
        return a;
    }

    // ===== billing / stats / “rooms to work” =====
    public static double billhousekeep(double total, String roomnum) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader("StatusofHotel.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("R") || line.startsWith("E") || line.startsWith("Z")) {
                    String code = line.split("\\s+")[0]; // e.g., R101
                    if (code.contains(roomnum)) {
                        char roomChar = code.charAt(0);
                        if (roomChar=='R') total += 22;
                        else if (roomChar=='E') total += 23;
                        else if (roomChar=='Z') total += 21;
                    }
                }
            }
        }
        return total;
    }
    public static void statistics(int vcount, int ocount, int ccount, int dcount, int rcount, int ucount, double total) throws IOException{
        PrintWriter s1 = new PrintWriter("HousekeepingStatistics.txt");
        s1.println("Congrats on finishing the week! Let's look at this week's statistics to see how it went!");
        s1.println("Total number of vacant rooms: " + vcount);
        s1.println("Total number of occupied rooms: " + ocount);
        s1.println(" ");
        s1.println("Total number of cleaned rooms: " + ccount);
        s1.println("Total number of dirty rooms: " + dcount);
        s1.println("Total number of requested rooms: " + rcount);
        s1.println("Total number of urgent rooms: " + ucount);
        s1.println(" ");
        s1.println("______________________________________");
        s1.println("Total bill of housekeeping this week: $" + total);
        s1.println("______________________________________");
        s1.close();
    }
    public static void roomsWork (int[] roomwc, String[] roomnum) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader("StatusofHotel.txt"))) {
            String status;
            int i = 0;
            while ((status = br.readLine()) != null && i < roomwc.length) {
                String[] parts = status.split("\\s+");
                String code = parts.length > 0 ? parts[0] : "";
                if (status.contains("Dirty")) {
                    roomwc[i]=2; roomnum[i]=code; i++;
                } else if (status.contains("Requested")) {
                    roomwc[i]=3; roomnum[i]=code; i++;
                } else if (status.contains("Urgent")) {
                    roomwc[i]=4; roomnum[i]=code; i++;
                }
            }
        }
        for (int i=0; i<roomwc.length; i++) if (roomwc[i]==2 && roomnum[i]!=null) System.out.println("\t"+roomnum[i]+"\tDirty");
        System.out.println();
        for (int i=0; i<roomwc.length; i++) if (roomwc[i]==3 && roomnum[i]!=null) System.out.println("\t"+roomnum[i]+"\tRequested");
        System.out.println();
        for (int i=0; i<roomwc.length; i++) if (roomwc[i]==4 && roomnum[i]!=null) System.out.println("\t"+roomnum[i]+"\tUrgent");
        System.out.println();
    }

    // ===== “dirty for 3 days -> urgent” =====
    public static void dirtycounter(int[] dcount, String[] housekeep){
        for (int i=0; i<housekeep.length; i++){
            if (dcount[i]<3){
                if ("Dirty".equals(housekeep[i])) dcount[i]++;
                else if (!"Dirty".equals(housekeep[i])) {
                    if (dcount[i]>0) dcount[i]=0;
                }
            } else if (dcount[i]==3){
                housekeep[i]="Urgent";
                dcount[i]=0;
            }
        }
    }
}
