package com.example.charlie.eegeo_programmingtest;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * Created by Charlie on 21/07/2016.
 */

public class FeatureKDTree{

    private static final int k = 2 ;
    private final Node tree;

    public FeatureKDTree(final List<FeatureHolder> features){
        final List<Node> nodes = new ArrayList<>(features.size());
        for(final FeatureHolder featureHolder : features){
            nodes.add(new Node(featureHolder));
        }
        tree = buildTree(nodes,0);
    }

    @Nullable
    public FeatureHolder findNearest(final int x,final  int y){
        final Node node = findNearest(tree, new Node (x, y),0);
        return node == null?null:node.feature;
    }


    @Nullable
    public FeatureHolder findFurthest(final int x,final  int y){
        final Node node = findFurthest(tree, new Node (x, y),0);
        return node == null?null:node.feature;
    }

    public FeatureHolder findMostIsolated(final  List<FeatureHolder>features){

        Node best = null;

        for(int i=0; i<features.size(); i++){
            Log.d("FEATURESTATS",features.get(i).name + " "+ features.get(i).x + " "+  features.get(i).y);
            Node current1 = new Node (features.get(i));
            if (best == null)
               best = new Node (features.get(i));// current1.euclideanDistance(findNearest(tree,new Node(features.get(i).x,features.get(i).x),0));
            else
                if(best != null)
                    if( best.euclideanDistance(findNearest(tree, new Node(best.feature.x,best.feature.y),0)) < current1.euclideanDistance(findNearest(tree,current1,0)) ){
                best = current1;

            }

            Log.d("CURRENTBESTDISTANCE", best.euclideanDistance(findNearest(tree,best,0))+"");
        }

        return best == null?null:best.feature;
    }

    private static Node findNearest(final Node current, final Node target, final int depth){
        final int axis = depth % k;
        final int direction = getComparator(axis).compare(target, current);
        final Node next = (direction < 0)? current.left : current.right;
        final Node other = (direction < 0)? current.right : current.left;

        Node best = (next == null)?current:findNearest(next,target,depth+1);
        if(current.euclideanDistance(target) < best.euclideanDistance(target) && current.euclideanDistance(target) != 0){
            best = current;
        }
        if(other != null)
        {
            if(current.verticalDistance(target, axis) < best.euclideanDistance(target)) {
                final Node possibleBest = findNearest(other, target, depth + 1);
                if(possibleBest.euclideanDistance(target) < best.euclideanDistance(target) && possibleBest.euclideanDistance(target) != 0){
                    best = possibleBest;
                }

            }
        }
        return best;
    }

    private static Node findFurthest(final Node current, final Node target, final int depth){
        final int axis = depth %k;
        final int direction = getComparator(axis).compare(target, current);
        final Node next = (direction < 0)? current.left : current.right;
        final Node other = (direction < 0)? current.right : current.left;

        Node best = (next == null)?current:findFurthest(next,target,depth+1);
        if(current.euclideanDistance(target) > best.euclideanDistance(target)){
            best = current;
        }

        return best;
    }

    @Nullable
    private static Node buildTree(final List<Node> items, final int depth){
        if(items.isEmpty()){
            return null;
        }

        Collections.sort(items, getComparator(depth % k));
        final int index = items.size() / 2;
        final Node root = items.get(index);
        root.left = buildTree(items.subList(0, index), depth + 1);
        root.right = buildTree(items.subList(index + 1, items.size()), depth + 1);
        return root;
    }

    private static class Node{
        Node left;
        Node right;
        FeatureHolder feature;
        final double[] point = new double[k];

        Node(final double x, final double y){
            Log.d("COORDS", x + " " + y);
            point[0] = (double) (cos(toRadians(x)) * cos(toRadians(y)));
            point[1] = (double) (cos(toRadians(x)) * sin(toRadians(y)));
        }

        Node(final FeatureHolder feature){
            this(feature.x, feature.y);
            this.feature = feature;

        }

        double euclideanDistance(final Node that){
            final double x = this.point[0] - that.point[0];
            final double y = this.point[1] - that.point[1];
            Log.d("DISTANCE",this.point[0] + " " + that.point[0]);
            return x * x + y * y;
        }

        double verticalDistance(final Node that, final int axis) {
            final double d = this.point[axis] - that.point[axis];
            return d * d;
        }
    }

    private static Comparator<Node> getComparator(final int i){

        return NodeComparator.values()[i];
    }

    private static enum NodeComparator implements Comparator<Node>{

        x {
            @SuppressLint("NewApi")
            @Override
            public int compare(final Node a, final Node b) {
                return Double.compare(a.point[0], b.point[0]);
            }
        },
        y {
            @SuppressLint("NewApi")
            @Override
            public int compare(final Node a, final Node b) {
                return Double.compare(a.point[1], b.point[1]);
            }
        }

    }

}
