package mino;

import main.KeyHandler;
import main.PlayManager;
import main.KeyHandler;

import java.awt.*;
import java.security.Key;

public class Mino {
    public Block b[]=new Block[4];
    public Block tempB[]=new Block[4];
    int autodropCounter=0;
    public boolean active=true;

    //direction can be 1/2/3/4
    public int direction=1;
    boolean leftCollision,rightCollision,bottomCollision;

    //deactivationg methods to help the mino slide over before it reaches the bottom although it may have touched other minos
    public boolean deactivating;
    int deactivatingCounter=0;

    public void create(Color c){
        b[0]=new Block(c);
        b[1]=new Block(c);
        b[2]=new Block(c);
        b[3]=new Block(c);

        tempB[0]=new Block(c);
        tempB[1]=new Block(c);
        tempB[2]=new Block(c);
        tempB[3]=new Block(c);
    }
    public void setXY(int x,int y){}
    public void updateXY(int direction){
        checkRotationCollision();
        if(leftCollision==false && rightCollision==false && bottomCollision==false){
            this.direction=direction;
            b[0].x=tempB[0].x;
            b[0].y=tempB[0].y;
            b[1].x=tempB[1].x;
            b[1].y=tempB[1].y;
            b[2].x=tempB[2].x;
            b[2].y=tempB[2].y;
            b[3].x=tempB[3].x;
            b[3].y=tempB[3].y;
        }
    }
    public void getDirection1(){}
    public void getDirection2(){}
    public void getDirection3(){}
    public void getDirection4(){}
    public void checkMovementCollision(){
        leftCollision=false;
        bottomCollision=false;
        rightCollision=false;

        //check static block collision
        checkStaticCollision();
        //left collision
        for(int i=0;i<b.length;i++){
            if(b[i].x==PlayManager.left_x) leftCollision=true;
        }
        for(int i=0;i<b.length;i++){
            if(b[i].x+Block.SIZE==PlayManager.right_x) rightCollision=true;
        }
        for(int i=0;i<b.length;i++){
            if(b[i].y+Block.SIZE==PlayManager.bottom_y) bottomCollision=true;
        }

    }
    public void checkRotationCollision(){
        leftCollision=false;
        bottomCollision=false;
        rightCollision=false;

        //check static block collision
        checkStaticCollision();

        //left collision
        for(int i=0;i<b.length;i++){
            if(tempB[i].x < PlayManager.left_x) leftCollision=true;
        }
        for(int i=0;i<b.length;i++){
            if(tempB[i].x+Block.SIZE > PlayManager.right_x) rightCollision=true;
        }
        for(int i=0;i<b.length;i++){
            if(tempB[i].y+Block.SIZE > PlayManager.bottom_y) bottomCollision=true;
        }
    }

    private void checkStaticCollision(){
        for(int i=0;i<PlayManager.staticBlocks.size();i++){
            int targetX=PlayManager.staticBlocks.get(i).x;
            int targetY=PlayManager.staticBlocks.get(i).y;

            //check down
            for(int ii=0;ii<b.length;ii++){
                if(b[ii].y+Block.SIZE==targetY && b[ii].x==targetX){
                    bottomCollision=true;
                }
            }
            //check left
            for(int ii=0;ii<b.length;ii++){
                if(b[ii].y==targetY && b[ii].x-Block.SIZE==targetX){
                    leftCollision=true;
                }
            }
            //check right
            for(int ii=0;ii<b.length;ii++){
                if(b[ii].y==targetY && b[ii].x+Block.SIZE==targetX){
                    rightCollision=true;
                }
            }
        }
    }

    public void update(){
        if(deactivating){
            deactivating();
        }
        //move the mino
        if(KeyHandler.upPressed){
            switch(direction){
                case 1:getDirection2(); break;
                case 2:getDirection3();break;
                case 3:getDirection4();break;
                case 4:getDirection1();break;
            }
            KeyHandler.upPressed=false;
        }

        checkMovementCollision();

        if(KeyHandler.downPressed){
            //mino can go down only if it is not hitting the bottom
            if(bottomCollision==false){
                b[0].y+=Block.SIZE;
                b[1].y+=Block.SIZE;
                b[2].y+=Block.SIZE;
                b[3].y+=Block.SIZE;
                //when the mino moves down we need to reset the autodropCounter
                autodropCounter=0;
            }
            KeyHandler.downPressed=false;
        }
        if(KeyHandler.leftPressed){
            if(leftCollision==false){
                b[0].x-=Block.SIZE;
                b[1].x-=Block.SIZE;
                b[2].x-=Block.SIZE;
                b[3].x-=Block.SIZE;
            }
            KeyHandler.leftPressed=false;
        }
        if(KeyHandler.rightPressed){
            if(rightCollision==false){
                b[0].x+=Block.SIZE;
                b[1].x+=Block.SIZE;
                b[2].x+=Block.SIZE;
                b[3].x+=Block.SIZE;
            }
            KeyHandler.rightPressed=false;
        }

        //stop the mino if the bottom collision takes place
        if(bottomCollision){
            deactivating=true;
        }else{
            autodropCounter++;
            if(autodropCounter== PlayManager.dropInterval){
                b[0].y+=Block.SIZE;
                b[1].y+=Block.SIZE;
                b[2].y+=Block.SIZE;
                b[3].y+=Block.SIZE;
                autodropCounter=0;
            }
        }
    }

    private void deactivating() {
        deactivatingCounter++;
        if(deactivatingCounter==45){
            deactivatingCounter=0;
            //check if the mino is still hitting the bottom of the floor
            checkMovementCollision();
            //if yes,then deactivate the mino
            if(bottomCollision) active=false;
        }
    }

    public void draw(Graphics2D g2){
        g2.setColor(b[0].c);
        int margin=2;
        g2.fillRect(b[0].x+margin,b[0].y+margin,Block.SIZE-(margin*2),Block.SIZE-(margin*2));
        g2.fillRect(b[1].x+margin,b[1].y+margin,Block.SIZE-(margin*2),Block.SIZE-(margin*2));
        g2.fillRect(b[2].x+margin,b[2].y+margin,Block.SIZE-(margin*2),Block.SIZE-(margin*2));
        g2.fillRect(b[3].x+margin,b[3].y+margin,Block.SIZE-(margin*2),Block.SIZE-(margin*2));
    }
}
