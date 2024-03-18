package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager {
    //Main play area
    final int WIDTH=360;
    final int HEIGHT=600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //MINO
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;

    //next mino
    Mino nextMino;
    final int NEXT_MINO_X;
    final int NEXT_MINO_Y;
    public static ArrayList<Block> staticBlocks=new ArrayList<>();

    //others
    public static int dropInterval=60;

    //effects
    int effectCounter=0;
    boolean effectOn;

    boolean gameOver;
    ArrayList<Integer> effect=new ArrayList<>();
    public PlayManager(){
        left_x=(GamePanel.WIDTH/2)-(WIDTH/2);
        right_x=left_x+WIDTH;
        top_y=50;
        bottom_y=top_y+HEIGHT;
        MINO_START_X=left_x+(WIDTH/2)- Block.SIZE;
        MINO_START_Y=top_y+Block.SIZE;

        NEXT_MINO_X=right_x+175;
        NEXT_MINO_Y=top_y+500;

        //set the starting mino
        currentMino=pickMino();
        currentMino.setXY(MINO_START_X,MINO_START_Y);

        //next mino
        nextMino=pickMino();
        nextMino.setXY(NEXT_MINO_X,NEXT_MINO_Y);
    }
    private Mino pickMino(){
        Mino mino = null;
        int c= new Random().nextInt(7);
        switch (c){
            case 0: mino = new Mino_L1();break;
            case 1: mino = new Mino_L2(); break;
            case 2: mino = new Mino_Bar(); break;
            case 3: mino=new Mino_Square();break;
            case 4: mino=new Mino_T();break;
            case 5: mino=new Mino_Z1();break;
            case 6: mino=new Mino_Z2();break;
        }
        return mino;
    }

    public void update(){
        if(currentMino.active==false){
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            if((currentMino.b[0].x==MINO_START_X) && (currentMino.b[0].y==MINO_START_Y)){
                gameOver=true;
            }

            currentMino.deactivating=false;

            //replace the current mino with the next mino
            currentMino=nextMino;
            currentMino.setXY(MINO_START_X,MINO_START_Y);
            nextMino=pickMino();
            nextMino.setXY(NEXT_MINO_X,NEXT_MINO_Y);
            //if any continuous collection of rows has 12 blocks in it then that rows need to be deleted
            checkDelete();
        }else{
            currentMino.update();
        }

    }

    private void checkDelete() {
        int blockCount=0;
        int x=left_x;
        int y=top_y;
        while(x<right_x && y<bottom_y){
            for(int i=0;i<staticBlocks.size();i++){
                if(staticBlocks.get(i).x==x && staticBlocks.get(i).y==y){
                    blockCount++;
                }
            }
            x+=Block.SIZE;

            if(x==right_x){
                if(blockCount==12){
                    effectOn=true;
                    effect.add(y);
                    for(int j=staticBlocks.size()-1;j>-1;j--){
                        if(staticBlocks.get(j).y==y){
                            staticBlocks.remove(j);
                        }
                    }
                    for(int j=0;j<staticBlocks.size();j++){
                        if(staticBlocks.get(j).y<y){
                            staticBlocks.get(j).y+=Block.SIZE;
                        }
                    }
                }
                x=left_x;
                y+=Block.SIZE;
                blockCount=0;
            }
        }
    }

    public void draw(Graphics2D g2){
        //draw play area frame
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4,top_y-4,WIDTH+8,HEIGHT+8);

        //draw next mino frame
        int x=right_x+100;
        int y=bottom_y-200;
        g2.drawRect(x,y,200,200);
        g2.setFont(new Font("Arial",Font.PLAIN,30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT",x+60,y+60);

        //draws the current mino
        if(currentMino!=null){
            currentMino.draw(g2);
        }

        //draw the next mino
        nextMino.draw(g2);

        //draw the static blocks
        for(int i=0;i<staticBlocks.size();i++){
            staticBlocks.get(i).draw(g2);
        }

        if(effectOn==true){
            effectCounter++;
            g2.setColor(Color.red);
            for (int i=0;i<effect.size();i++){
                g2.fillRect(left_x,effect.get(i),WIDTH,Block.SIZE);
            }
            if(effectCounter==10){
                effectCounter=0;
                effectOn=false;
                effect.clear();
            }
        }
        //draw the pause
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver){
            x=left_x+25;
            y=top_y+320;
            g2.drawString("Game Over",x,y);
        }
        else if(KeyHandler.pausePressed==true){
            x= left_x+70;
            y=top_y+320;
            g2.drawString("PAUSED",x,y);
        }

        //draw the title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Times New Roman", Font.ITALIC,60));
        g2.drawString("Simple Tetris",35,320);
    }
}
