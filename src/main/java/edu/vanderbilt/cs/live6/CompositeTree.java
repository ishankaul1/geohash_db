package edu.vanderbilt.cs.live6;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Binary tree to follow the composite pattern for the assignment. Contains CompositeNode branch nodes and
 * CompositeTree leaves.
 */
public class CompositeTree {
    private CompositeNode root;
    private int maxBitsOfPrecision;
    private GeoHashFactory geoHashFactory;

    /**
     * Creates instance of the composite tree. Bits of precision can't be less than 0.
     * @param maxBitsOfPrecision
     */
    public CompositeTree(int maxBitsOfPrecision){

        this.maxBitsOfPrecision = maxBitsOfPrecision;
        this.root = new CompositeNode();
        this.geoHashFactory = new GeoHashFactory_Impl();
    }

    public CompositeTree(int maxBitsOfPrecision, GeoHashFactory geoHashFactory){
        this.maxBitsOfPrecision = maxBitsOfPrecision;
        this.root = new CompositeNode();
        this.geoHashFactory = geoHashFactory;
    }

    /**
     * Inserts a data and position into the database
     * @param dataAndPosition
     */
    public void insert(DataAndPosition dataAndPosition){
        /**if (hashCode.length != this.maxBitsOfPrecision) {
         throw new IllegalArgumentException("faulty hash");
         }*/
        //start at root
        //create the double
        //follow hashcode down to bits of precision, making children as we need
        //at depth = bitsofPrecision, create a leaf node and store the double
        int curDepth = 0;
        //create hashCode here
        Iterator<Boolean> geoHashIterator = geoHashFactory.with(dataAndPosition.getLatitude(), dataAndPosition.getLongitude(), this.maxBitsOfPrecision).iterator();

        insert_helper(this.root, geoHashIterator, dataAndPosition, curDepth);
    }

    /**
     * recursive function to handle iterating down the composite tree to insert a hashcode
     * @param geoHashIterator
     * @param curDepth
     */
    private boolean insert_helper(CompositeNode node, Iterator<Boolean> geoHashIterator, DataAndPosition dataAndPosition, int curDepth){
        //base case - we are about to create the leaf node
        boolean leafAdded; // whether leaf is added down the line
        boolean childAdded = false; //whether immediate child is added
        Boolean cur_bool = geoHashIterator.next();
        if (!geoHashIterator.hasNext()){//curDepth == this.maxBitsOfPrecision-1){
            //String hashString = hashCode.toString();
            CompositeLeaf leaf = new CompositeLeaf(dataAndPosition);
            if (cur_bool){//hashCode.getGeoHash().get(curDepth)) {
                //make leaf node to the right
                leafAdded = node.addRight(leaf);
                if (leafAdded) {
                    node.addLeaf();
                }else{
                    //didnt add a node - must have already existed
                    node.getRight().addPosition(dataAndPosition);
                }
            }else{
                //make leaf node to the left
                leafAdded = node.addLeft(leaf);
                //node.numLeaves += 1;
                if (leafAdded){
                    node.addLeaf();
                }else{
                    node.getLeft().addPosition(dataAndPosition);
                }
            }

            return leafAdded;

        }
        //if hash bit true, go right; else, go left
        if (cur_bool){///hashCode.getGeoHash().get(curDepth)){
            //go right
            //if right already exists just go right
            if (node.rightExists()){
                leafAdded = insert_helper(node.right, geoHashIterator, dataAndPosition, curDepth+1);
            }else{
                //right doesn't exist - make a new one
                CompositeNode child = new CompositeNode();
                //if immedate right isn't added but one gets added farther down the line; increment numleaves by 1
                childAdded = node.addRight(child);
                //node.numLeaves += 1;
                node.addLeaf();
                leafAdded = insert_helper(node.right, geoHashIterator, dataAndPosition, curDepth+1);

            }
            if (!childAdded && leafAdded){
                //node.numLeaves += 1;
                node.addLeaf();
            }
            return leafAdded;

        }else{
            //go left
            //if left already exists just go left
            if (node.leftExists()){
                leafAdded = insert_helper(node.left, geoHashIterator, dataAndPosition, curDepth+1);
            }else{
                //left doesn't exist - make a new one
                CompositeNode child = new CompositeNode();
                childAdded = node.addLeft(child);
                //node.numLeaves += 1;
                node.addLeaf();
                leafAdded = insert_helper(node.left, geoHashIterator, dataAndPosition, curDepth+1);
            }
            if (!childAdded && leafAdded){
                //node.numLeaves += 1;
                node.addLeaf();
            }
            return leafAdded;

        }

    }

    /**
     * Deletes a leaf node from the composite tree
     * @param
     * @return
     */
    public Collection<DataAndPosition> delete(Position position){
        /**if (hashCode.length != this.maxBitsOfPrecision){
         throw new IllegalArgumentException("Faulty hashcode");
         }*/
        int curDepth = 0;
        Iterator<Boolean> geoHashIterator = geoHashFactory.with(position.getLatitude(), position.getLongitude(), this.maxBitsOfPrecision).iterator();
        if (!geoHashIterator.hasNext()){
            //delete from the first itself
            return this.root.deletePosition(position);
        }
        return delete_helper(this.root, position, geoHashIterator, curDepth);

    }

    public ArrayList<DataAndPosition> delete_helper(CompositeNode node, Position position, Iterator<Boolean> geoHashIterator, int curDepth){
        Boolean cur_bool = geoHashIterator.next();
        ArrayList<DataAndPosition> deletedPositions = new ArrayList<>();
        //base case - next node is leaf. delete it and return true
        if (!geoHashIterator.hasNext()){//curDepth == this.maxBitsOfPrecision-1){
            if(cur_bool){//hashCode.getGeoHash().get(curDepth)){ //delete the node on the right
                if (node.rightExists()){
                    deletedPositions.addAll(node.getRight().deletePosition(position));//.deleteRight() != null){
                    //a position was deleted
                    //node.numLeaves -= 1;
                    if (node.getRight().getNumLeaves() == 0){ //leaf node has no more positions - delete
                        node.deleteRight();
                        //node.numLeaves -= 1;
                        node.decrLeaves(1);
                    }
                    //node.decrLeaves(1);
                }
                return deletedPositions;
            }else{//delete the node on the left
                if (node.leftExists()){// && node.getLeft().deletePosition(position)){//node.deleteLeft() != null){
                    //node.numLeaves -=1;
                    deletedPositions.addAll(node.getLeft().deletePosition(position));
                    if (node.getLeft().getNumLeaves() == 0) { //leaf has no more positions - delete
                        node.deleteLeft();
                        node.decrLeaves(1);
                    }

                }
                return deletedPositions;

            }
        }

        if (cur_bool){//hashCode.getGeoHash().get(curDepth)){//delete to right
            if (node.rightExists()){
                deletedPositions.addAll(delete_helper(node.getRight(), position, geoHashIterator, curDepth));
                //if (delete_helper(node.right, position, geoHashIterator, curDepth+1)){ //a position was deleted
                if (node.getRight().numLeaves == 0){//right leaf now empty
                    node.deleteRight();
                    node.decrLeaves(1);
                }

            }
            return deletedPositions;
        }else{ //delete to left
            if (node.leftExists()){
                deletedPositions.addAll(delete_helper(node.left, position, geoHashIterator, curDepth+1));
                if (node.getLeft().numLeaves == 0){
                    node.deleteLeft();
                    node.decrLeaves(1);
                }
            }
            return deletedPositions;


            /**
             if(node.left.getNumLeaves() == 1){
             // only 1 leaf - can just delete the whole subtree
             node.deleteLeft();
             //node.numLeaves -= 1;
             node.decrLeaves(1);
             return true;
             }else{
             if (delete_helper(node.left, hashCode, curDepth+1)){
             //deleted - subtract a leaf
             //node.numLeaves -=1;
             node.decrLeaves(1);
             return true;
             }else{
             //node not deleted - ret false
             return false;
             }
             }*/


        }
    }

    /**
     * returns true if the specified hashcode or hashcode prefix exists in the subtree
     * @param
     * @return
     */
    public boolean contains(Position pos, int bitsOfPrecision){
        Iterator<Boolean> geoHashIterator = geoHashFactory.with(pos.getLatitude(), pos.getLongitude(), bitsOfPrecision).iterator();
        if (!geoHashIterator.hasNext()){
            if (root.getNumLeaves() == 0){
                return false;

            }else{
                return true;
            }
        }
        int curDepth = 0;
        return contains_helper(this.root, pos, geoHashIterator, curDepth);

    }

    public boolean contains_helper(CompositeNode node, Position pos, Iterator<Boolean> geoHashIterator, int curDepth){
        //base case - have hit depth
        Boolean cur_bool = geoHashIterator.next();
        if (!geoHashIterator.hasNext()){//curDepth == hashCode.getGeoHash().size()){
            if (node.getNumLeaves() > 0) {
                return true;
            }else{
                return false;
            }
        }


        if (cur_bool){//hashCode.getGeoHash().get(curDepth)){// next step is go right
            if (node.rightExists()){
                return contains_helper(node.right, pos, geoHashIterator, curDepth+1);
            }else{
                return false;
            }
        }else{//next step is go left
            if (node.leftExists()){
                return contains_helper(node.left, pos, geoHashIterator, curDepth+1);
            }else{
                return false;
            }
        }
    }

    /**
     * Deletes all leaves underneath/including the specified hashCode prefix
     * @param
     * @return
     */
    public Collection<DataAndPosition> deleteAll(Position pos, int bitsOfPrecision){
        if (bitsOfPrecision == 0){
            NearbyGatherRet ret =  nearby_gather(this.root);
            this.root.deleteLeft();
            this.root.deleteRight();
            //this.root.numLeaves = 0;
            this.root.decrLeaves(this.root.getNumLeaves());
            return ret.getPositions();
        }
        Iterator<Boolean> geoHashIterator = geoHashFactory.with(pos.getLatitude(), pos.getLongitude(), bitsOfPrecision).iterator();
        return deleteAll_find(this.root, pos, geoHashIterator, 0).getPositions();



    }

    /**
     *
     * @param node
     * @param
     * @param curDepth
     * @return
     */
    private NearbyGatherRet deleteAll_find(CompositeNode node, Position pos,Iterator<Boolean> geoHashIterator, int curDepth){
        NearbyGatherRet ret;//ArrayList<String> leaves = new ArrayList<>();
        if (node == null){
            ret = new NearbyGatherRet();
            return ret;
        }
        Boolean cur_bool = geoHashIterator.next();
        //base case - reach the node before where to start from
        if (!geoHashIterator.hasNext()){//curDepth == hashCode.getGeoHash().size()-1){
            if (cur_bool){//hashCode.getGeoHash().get(curDepth)){
                //node to start gather from is on the right
                ret = nearby_gather(node.right);
                node.decrLeaves(ret.getNumLeaves());
                //node.numLeaves -= leaves.size();
                node.deleteRight(); //delete everything starting from the prefix
                return ret;
            }else{
                //node to start gather from is on left
                ret = nearby_gather(node.left);
                //node.numLeaves -= leaves.size();
                node.decrLeaves(ret.getNumLeaves());
                node.deleteLeft(); //delete everything starting from the prefix
                return ret;
            }
        }
        if (cur_bool){//hashCode.getGeoHash().get(curDepth)) {
            //recurse to the right
            ret = deleteAll_find(node.right, pos, geoHashIterator, curDepth);//leaves.addAll(deleteAll_find(node.right, hashCode, curDepth + 1));
            if (node.getRight().getNumLeaves() == 0) {
                node.deleteRight();
            }
            //node.numLeaves -= leaves.size();
            node.decrLeaves(ret.getNumLeaves());
            return ret;

        }else {
            ret = deleteAll_find(node.left, pos, geoHashIterator, curDepth);//leaves.addAll(deleteAll_find(node.left, hashCode, curDepth + 1));
            if (node.getLeft().getNumLeaves() ==0) {
                node.deleteLeft();
            }
            //node.numLeaves -= leaves.size();
            node.decrLeaves(ret.getNumLeaves());
            return ret;
        }


    }

    /**
     * Returns all leaves underneath/including the specified hashCode prefix
     * @param
     * @return
     */
    public Collection<DataAndPosition> nearby(Position pos, int bitsOfPrecision){
        //first step - iterate down to node specified by hashcode
        //If node is leaf; return a list with just the leaf
        //if not leaf, call new recursive function to return all leaves starting from that node
        Iterator<Boolean> geoHashIterator = geoHashFactory.with(pos.getLatitude(), pos.getLongitude(), bitsOfPrecision).iterator();
        return nearby_find(this.root, geoHashIterator, 0);

    }

    /**
     * Iterates down to the specified hashCode calls the function to grab all leaves from that node
     * If the hashcode is a leaf then just return a list of that one leaf
     * @param node
     * @param
     * @param curDepth
     * @return
     */
    private Collection<DataAndPosition> nearby_find(CompositeNode node, Iterator<Boolean> geoHashIterator, int curDepth){
        if (node == null){
            return null;
        }
        if(!geoHashIterator.hasNext()){//curDepth == hashCode.getGeoHash().size()){
            //if we're at leaf
            /**if (node.isLeaf()) {
             ArrayList<String> leaf = new ArrayList<>();
             leaf.add((String)node.getHashString());
             return leaf;
             }*/
            return nearby_gather(node).getPositions();
        }
        Boolean cur_bool = geoHashIterator.next();
        //base case - we're at that hashCode

        //otherwise continue recursion
        if (cur_bool){//hashCode.getGeoHash().get(curDepth)){
            //go right
            return nearby_find(node.right, geoHashIterator, curDepth+1);
        }else{
            return nearby_find(node.left, geoHashIterator, curDepth+1);
        }
    }

    /**
     * recursively gathers all leaves under the specified node into a list
     * @param node
     * @return
     */
    private NearbyGatherRet nearby_gather(CompositeNode node){//Collection<DataAndPosition> nearby_gather(CompositeNode node){
        //ArrayList<DataAndPosition> positions = new ArrayList<>();
        NearbyGatherRet thisRet = new NearbyGatherRet();
        if (node == null)
            return thisRet;
        else if (node.isLeaf()){
            thisRet.addAll(node.data());
            thisRet.addLeaves(1);
            //leaves.add(node.data());
        }else{

            NearbyGatherRet rightRet = nearby_gather(node.getRight());
            NearbyGatherRet leftRet = nearby_gather(node.getLeft());
            thisRet.addAll(rightRet.getPositions());
            thisRet.addAll(leftRet.getPositions());
            thisRet.addLeaves(rightRet.getNumLeaves() + leftRet.getNumLeaves());
        }
        return thisRet;

    }

    //for testing purposes - need to move this to junit test eventually
    public static ArrayList<Boolean> boolArrayToArrayList(boolean[] boolArray){
        ArrayList<Boolean> boolList = new ArrayList<Boolean>(boolArray.length);
        for (boolean bool : boolArray){
            boolList.add(bool);
        }
        return boolList;
    }
    public static void main(String[] args) {
        //Testing script for composite tree functionality
        /**CompositeTree compositeTree = new CompositeTree(5);




         GeoHashImpl insert1 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{true, true, true, true, true}));
         GeoHashImpl containsCheck1 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{true, true}));
         GeoHashImpl insert2 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{true, false, true, false, true}));
         GeoHashImpl containsCheck2 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{true, false, true}));
         GeoHashImpl containsCheck3 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{true}));
         GeoHashImpl containsCheck4 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{false}));
         GeoHashImpl insert3 = GeoHashImpl.fromBoolArray( boolArrayToArrayList(new boolean[]{false, false, false, false, false}));
         GeoHashImpl nearbyTrue = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[] {true, true, true}));
         GeoHashImpl nearbyTrue2 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{true}));
         GeoHashImpl deleteall = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{true}));
         GeoHashImpl insert4 = GeoHashImpl.fromBoolArray(boolArrayToArrayList(new boolean[]{false, false, false, false, true}));



         //test insertion - basic case
         System.out.println("INSERTING: 11111");
         System.out.println("----------------");
         compositeTree.insert(insert1);
         System.out.println("Number of leaves in root (exp 1): " + compositeTree.root.getNumLeaves());
         System.out.println("Contains 1 (expected: true): " + compositeTree.contains(containsCheck1));
         //test insertion branch overlap
         System.out.println("\nINSERTING: 10101");
         System.out.println("----------------");
         compositeTree.insert(insert2);
         System.out.println("Number of leaves in root (exp 2): " + compositeTree.root.getNumLeaves());
         System.out.println(("Contains 2 (expected: true): " + compositeTree.contains(containsCheck2)));
         System.out.println("Contains 3 (expected: true): " + compositeTree.contains(containsCheck3));
         System.out.println("Contains 4 (expected: false): " + compositeTree.contains(containsCheck4));
         System.out.println("Check proper branch numleaves (exp: 2): " + compositeTree.root.right.getNumLeaves());
         System.out.println("Check proper branch numleaves (exp: 1): " + compositeTree.root.right.right.getNumLeaves());
         System.out.println("Check proper branch numleaves (exp: 1): " + compositeTree.root.right.left.getNumLeaves());
         //test insertion overwrite
         System.out.println("\nINSERTING (overwrite): 11111");
         System.out.println("----------------");
         compositeTree.insert(insert1);
         System.out.println("Number of leaves in root (exp 2): " + compositeTree.root.getNumLeaves());
         System.out.println("Check proper branch numleaves (exp: 2): " + compositeTree.root.right.getNumLeaves());
         System.out.println("Check proper branch numleaves (exp: 1): " + compositeTree.root.right.right.getNumLeaves());

         //test delete existing
         System.out.println("\nDELETING: 11111");
         System.out.println("----------------");
         compositeTree.delete(insert1);
         System.out.println(("Contains 1 (expected: false): " + compositeTree.contains(containsCheck1)));
         System.out.println(("Contains 2 (expected: true): " + compositeTree.contains(containsCheck2)));
         System.out.println("Number of leaves in root (exp 1): " + compositeTree.root.getNumLeaves());
         System.out.println("Check proper branch numleaves (exp: 1): " + compositeTree.root.right.getNumLeaves());
         System.out.println("Check that whole branch was deleted starting from root.right.right (exp: false)" + compositeTree.root.right.rightExists());

         //test delete existing 2
         System.out.println("\nDELETING: 10101");
         System.out.println("----------------");
         compositeTree.delete(insert2);
         System.out.println("Contains 2: (expected: false): " + compositeTree.contains(containsCheck2));
         System.out.println("Make sure root has 0 leaves: " + compositeTree.root.getNumLeaves());
         System.out.println("Make sure root children dont exist (exp - false): " + compositeTree.root.rightExists());

         //test nearby and deleteAll

         compositeTree.insert(insert1);
         compositeTree.insert(insert2);
         compositeTree.insert(insert3);
         System.out.println("Make sure root has 3 leaves: " + compositeTree.root.getNumLeaves());
         System.out.println("Check if nearby works (exp: 1)" + compositeTree.nearby(nearbyTrue).size());
         System.out.println("Check if nearby works (exp: 2)" + compositeTree.nearby(nearbyTrue2).size());
         compositeTree.deleteAll(deleteall);
         System.out.println("Make sure right tree deleted (exp: false)" + compositeTree.root.rightExists());
         System.out.println("Make sure root has only 1 leaf: " + compositeTree.root.getNumLeaves());

         //corner case
         System.out.println("Check numleaves of falsex4 (exp: 1): " + compositeTree.root.left.left.left.left.getNumLeaves());

         compositeTree.insert(insert4);
         System.out.println("Check numleaves of falsex4 (exp: 2): " + compositeTree.root.left.left.left.left.getNumLeaves());
         compositeTree.delete(insert3);
         System.out.println("Check numleaves of falsex4 (exp: 1): " + compositeTree.root.left.left.left.left.getNumLeaves());




         */


    }


}

class NearbyGatherRet{
    private ArrayList<DataAndPosition> positions;
    private int numLeaves; //number of leaves from under the current node

    public NearbyGatherRet(){
        this.positions = new ArrayList<>();
        this.numLeaves = 0;
    }

    public void addLeaves(int num){
        this.numLeaves += num;
    }

    public void addAll(Collection<DataAndPosition> positions1){
        this.positions.addAll(positions1);
    }
    public ArrayList<DataAndPosition> getPositions(){
        return this.positions;
    }

    public int getNumLeaves(){
        return this.numLeaves;
    }

}

class CompositeNode{
    protected int numLeaves; //the number of leaves underneath this node in the tree; always initialized to 1
    CompositeNode left; //represents a 0
    CompositeNode right; // represents a 1

    public CompositeNode(){
        this.numLeaves = 0;
        this.left = null;
        this.right = null;
    }

    public boolean isLeaf(){
        return false;
    }

    public List<DataAndPosition> data (){
        return null;
    }


    public boolean addLeft(CompositeNode child){
        if (!leftExists()) {
            this.left = child;
            //this.numLeaves += 1;
            return true;
        }
        return false;

    }

    public boolean addRight(CompositeNode child) {
        if (!rightExists()) {
            this.right = child;
            //this.numLeaves += 1;
            return true;
        }
        return false;
    }
    public boolean leftExists(){
        return !(this.left == null);
    }

    public boolean rightExists(){
        return !(this.right == null);
    }

    public CompositeNode getRight(){
        return this.right;
    }

    public CompositeNode getLeft(){
        return this.left;
    }

    public ArrayList<DataAndPosition> deletePosition(Position position){
        return null;
    }

    public CompositeNode deleteLeft(){
        if (leftExists()) {
            CompositeNode temp = this.left;
            this.left = null;
            //this.numLeaves--;
            return temp;

        }
        return null;
    }

    public void addPosition(DataAndPosition pos){
        return;
    }

    public CompositeNode deleteRight(){
        if (rightExists()) {
            CompositeNode temp = this.right;
            this.right = null;
            //this.numLeaves--;
            return temp;
        }
        return null;
    }

    public void addLeaf(){
        this.numLeaves += 1;
    }

    public void decrLeaves(int decr){
        this.numLeaves -= decr;
    }



    public int getNumLeaves(){
        return this.numLeaves;
    }



}

class CompositeLeaf extends CompositeNode{
    List<DataAndPosition> positions;
    //since these can't actually have leaves, use this.numLeaves to keep easy access to number of dataAndPositions in
    //this node

    //only initialize with one data and position at a time
    public CompositeLeaf(DataAndPosition dataAndPosition){
        super();
        positions = new ArrayList<>();
        positions.add(dataAndPosition);
        this.numLeaves+=1;
    }

    @Override
    public List<DataAndPosition> data(){
        return this.positions;
    }

    @Override
    public void addPosition(DataAndPosition position){
        this.numLeaves += 1;
        positions.add(position);
    }

    @Override
    public ArrayList<DataAndPosition> deletePosition(Position position){
        Iterator<DataAndPosition> dataAndPositionIterator = this.positions.iterator();
        ArrayList<DataAndPosition> deletedPositions = new ArrayList<>();
        while (dataAndPositionIterator.hasNext()){
            DataAndPosition position1 = dataAndPositionIterator.next();
            if (position1.getLongitude() == position.getLongitude() && position1.getLatitude() == position.getLatitude()){
                deletedPositions.add(position1);
                dataAndPositionIterator.remove();
                this.numLeaves--;
            }
        }
        return deletedPositions;


    }

    @Override
    public boolean addLeft(CompositeNode child){
        System.out.println("Can't add children to a leaf");
        return false;
    }

    @Override
    public boolean addRight(CompositeNode child){
        System.out.println("Can't add children to a leaf");
        return false;
    }

    @Override
    public boolean leftExists(){
        return false;
    }

    @Override
    public boolean rightExists(){
        return false;
    }



    @Override
    public boolean isLeaf(){
        return true;
    }

    @Override
    public void addLeaf(){
        return;
    }

    @Override
    public void decrLeaves(int decr){
        return;
    }

    //deleteLeft and deleteright don't need to be overrided because left and right will never exist, and
    //the constructor already handles that



}


