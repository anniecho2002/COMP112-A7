/* Code for COMP-102-112 - 2021T1, Assignment 7
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

import ecs100.*;
import java.awt.Color;
import javax.swing.JColorChooser;
import java.awt.event.MouseAdapter;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * A simple drawing program.
 * The user can select from a variety of tools and options using the buttons and
 *   elements down the left side, and can use the mouse to add elements to the drawing
 *   according to the current tool and options
 * Note, most of the "action" in the program happens in response to mouse events;
 *   the buttons, textFields, and sliders mostly record information that is used
 *   later by the mouse responding.
 */


public class MiniPaint{
    // fields to remember:
    //  - the "tool" - what will be drawn when the mouse is next released.
    //                 may be a shape, or an image, or a caption,
    //    [Completion] or freehand, or eraser, or flower
    //  - whether the shape should be filled or not
    //  - the position the mouse was pressed,
    //  - the string for the text caption
    //  - the width of the lines and the font size of the text captions.
    //  - [Completion] the name of the image file
    //  - [Completion] the colors for the border and fill for shapes and captions

    private String tool = "Line"; // the current tool, governing what the mouse will do.
    private double initialX, initialY;
    private double releaseX, releaseY;
    private double drawX, drawY;
    private String fillName = "No Fill";
    private boolean fill = false;
    private Color lineColor = Color.blue;
    private Color fillColor = Color.red;
    private String caption = "";
    private double lineWidth = 1;
    private double textSize = 15;
    private String fileName;
    private boolean initialClick = true; // whether or not the click will be the initial click
    
    
    /**
     * Set up the interface: buttons, textfields, sliders,
     * listening to the mouse
    */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearGraphics);
        UI.addButton("Line", this::doSetLine); 
        UI.addButton("Rectangle", this::doSetRect); 
        UI.addButton("Oval", this::doSetOval);
        UI.addTextField("Caption",this::doSetCaption);
        UI.addButton("Image", this::setImage);
        UI.addButton("Eraser", this::setEraser);
        UI.addButton("Flower", this::setFlower);
        UI.addButton("Fill/No Fill", this::doSetFill);
        UI.addButton("Line Color", this::doSetLineColor);
        UI.addButton("Fill Color", this::doSetFillColor);
        UI.addSlider("Line Width:", 1, 20, 1, this::setLineWidth);
        UI.addSlider("Text Size:", 15, 50, 15, this::setTextSize);
        UI.setMouseMotionListener(this::doMouse);

        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);  // Hide the text area.
    }   
    
    /** Respond to the tool buttons */
    public void doSetLine(){
        tool = "Line";
    }
    public void doSetRect(){
        tool = "Rect";
    }
    public void doSetOval(){
        tool = "Oval";
    }
    public void doSetCaption(String userCaption){
        tool = "Caption";
        caption = userCaption;
    }
    public void doSetFill(){
        if (fill == true){
            fill = false;
            fillName = "No Fill";
        }
        else{
            fill = true;
            fillName = "Fill";
        }
    }
    public void doSetLineColor(){
        lineColor = JColorChooser.showDialog(null, "LineColor", Color.white);
    }
    public void doSetFillColor(){
        fillColor = JColorChooser.showDialog(null, "FillColor", Color.white);
    }
    public void setLineWidth(double userWidth){
        lineWidth = userWidth;
    }
    public void setTextSize(double userSize){
        textSize = userSize;
    }
    public void setImage(){
        fileName = UIFileChooser.open();
        tool = "Image";
    }
    public void setEraser(){
        tool = "Eraser";
    }
    public void setFlower(){
        tool = "Flower";
    }
    
    /**
     * Respond to mouse events
     * When pressed, remember the position.
     * When released, draw what is specified by current tool
     * Uses the value stored in the field to determine which kind of tool to draw.
     *  It should call the drawALine, drawARectangle, drawAnOval, etc, methods
     *  passing the pressed and released positions
     * [Completion] should respond to "dragged" events also to do erasing and freehand drawing
     */
    public void doMouse(String action, double x, double y) {
        UI.setLineWidth(lineWidth);
        UI.setColor(lineColor);
        if (tool.equals("Eraser") && action.equals("dragged")){
            UI.eraseOval(x,y,lineWidth,lineWidth);
        }
        if (action.equals("pressed")){
            initialX = x;
            initialY = y;
        }
        else if (action.equals("dragged")){
            if (tool.equals("Line")){
                UI.invertLine(initialX, initialY, x, y);
                UI.sleep(7);
                UI.invertLine(initialX, initialY, x, y);
            }
            if (tool.equals("Rect")||tool.equals("Oval") || tool.equals("Image")){
                double height = Math.abs(initialY-y);
                double length = Math.abs(initialX-x);
                if (initialX < x){
                    drawX = initialX;
                }
                if (initialX > x){
                    drawX = x;
                }
                if (initialY < y){
                    drawY = initialY;
                }
                if (initialY > y){
                    drawY = y;
                }
                if (tool.equals("Rect") ||tool.equals("Image")){
                    UI.invertRect(drawX, drawY, length, height);
                    UI.sleep(7);
                    UI.invertRect(drawX, drawY, length, height);
                }
                else if (tool.equals("Oval")){
                    UI.invertOval(drawX, drawY, length, height);
                    UI.sleep(7);
                    UI.invertOval(drawX, drawY, length, height);
                }
            }
            if (tool.equals("Caption")){
                UI.invertString(caption, x, y);
                UI.sleep(7);
                UI.invertString(caption, x, y);
            }
        }
        else if (action.equals("released")){
            releaseX = x;
            releaseY = y;
            double flowerXRad = Math.abs(initialX - releaseX);
            double flowerYRad = Math.abs(initialY - releaseY);
            double flowerRad = returnRad(flowerXRad, flowerYRad);
            if (tool.equals("Line")){
                drawALine(initialX, initialY, releaseX, releaseY);
            }
            if (tool.equals("Rect")){
                drawARectangle(initialX, initialY, releaseX, releaseY);
            }
            if (tool.equals("Oval")){
                drawAnOval(initialX, initialY, releaseX, releaseY);
            }
            if (tool.equals("Caption")){
                drawACaption(releaseX, releaseY);
            }
            if (tool.equals("Image")){
                drawAnImage(initialX, initialY, releaseX, releaseY);
            }
            if (tool.equals("Flower")){
                drawAFlower(initialX, initialY, flowerRad);
            }
        }
    }
    
    public double returnRad(double num1, double num2){
        if (num1>num2){
            return num1;
        }
        else{
            return num2;
        }
    }

    /**
     * Draw a line between the two positions (x1, y1) and (x2, y2)
     */
    public void drawALine(double x1, double y1, double x2, double y2){
        UI.setColor(lineColor);
        UI.setLineWidth(lineWidth);
        UI.drawLine(x1,y1,x2,y2);
    }

    /**
     * Draw a rectangle between the two diagonal corners
     * [Completion] Works out the left, top, width, and height 
     * Then draws the rectangle, based on the options
     */
    public void drawARectangle(double x1, double y1, double x2, double y2){
        UI.setColor(lineColor);
        UI.setLineWidth(lineWidth);
        double height = Math.abs(y1-y2);
        double length = Math.abs(x1-x2);
        if (x1 < x2){
            drawX = x1;
        }
        if (x1 > x2){
            drawX = x2;
        }
        if (y1 < y2){
            drawY = initialY;
        }
        if (y1 > y2){
            drawY = y2;
        }
        if (fill == false){
            UI.drawRect(drawX,drawY,length,height);
        }
        else{
            UI.setColor(fillColor);
            UI.fillRect(drawX,drawY,length,height);
            UI.setColor(lineColor);
            UI.drawRect(drawX,drawY,length,height);
        }
    }

    /**
     * Draw an oval to fit the rectangle between the the two diagonal corners
     * [Completion] Works out the left, top, width, and height 
     * Then draws the oval, based on the options
     */
    public void drawAnOval(double x1, double y1, double x2, double y2){
        UI.setColor(lineColor);
        UI.setLineWidth(lineWidth);
        double height = Math.abs(y1-y2);
        double length = Math.abs(x1-x2);
        if (x1 < x2){
            drawX = x1;
        }
        if (x1 > x2){
            drawX = x2;
        }
        if (y1 < y2){
            drawY = initialY;
        }
        if (y1 > y2){
            drawY = y2;
        }
        if (fill == false){
            UI.drawOval(drawX,drawY,length,height);
        }
        else{
            UI.setColor(fillColor);
            UI.fillOval(drawX,drawY,length,height);
            UI.setColor(lineColor);
            UI.drawOval(drawX,drawY,length,height);
        }
    }

    /** 
     * Draws the current caption at the mouse released point.
     */
    public void drawACaption(double x, double y){
        UI.setColor(lineColor);
        UI.setFontSize(textSize);
        UI.drawString(caption, x, y);
    }

    /** [Completion]
     * Draws the current image between the two diagonal corners, unless
     *  they are very close, and then just draws the image at its natural size
     *  Works out the left, top, width, and height 
     * Then draws the image, if there is one.
     */
    public void drawAnImage(double x1, double y1, double x2, double y2){
        double width = Math.abs(x1-x2);
        double height = Math.abs(y1-y2);
        if (width < 5 && height < 5){
            UI.drawImage(fileName,x2,y2);
        }
        else if (x2 < x1 && y2 < y1){
            UI.drawImage(fileName,x2,y2,width,height);
        }
        else if (x1 < x2 && y2 < y1){
            UI.drawImage(fileName,x1, y1-height, width, height);
        }
        else if (x1 > x2 && y1 < y2){
            UI.drawImage(fileName, x1-width, y1, width, height);
        }
        else{
            UI.drawImage(fileName,x1,y1,width,height);
        }
    }
    


    /** [Completion]
     * Draws a simple flower with 6 petals, centered at (x,y) with the given radius
     */
    public void drawAFlower(double x, double y, double radius){
        UI.setColor(Color.yellow); // centre of flower
        double circleDiam = radius*2/3;
        double circleRad = circleDiam/2;
        UI.fillOval(x-circleRad,y-circleRad,circleDiam,circleDiam); // centre circle
        UI.setColor(fillColor);
        UI.fillOval(x+circleRad, y-circleRad,circleDiam, circleDiam); // right circle
        UI.fillOval(x-circleRad-circleDiam, y-circleRad,circleDiam, circleDiam); // left circle
        UI.fillOval(x-circleDiam, y-circleRad+circleDiam-(circleRad/4),circleDiam, circleDiam);
        UI.fillOval(x, y-circleRad+circleDiam -(circleRad/4),circleDiam, circleDiam);
        UI.fillOval(x-circleDiam, y-circleRad-circleDiam+(circleRad/4),circleDiam, circleDiam);
        UI.fillOval(x, y-circleRad-circleDiam+(circleRad/4),circleDiam, circleDiam);

    }


    // Main:  constructs a new MiniPaint object and set up GUI
    public static void main(String[] arguments){
        MiniPaint mp = new MiniPaint();
        mp.setupGUI();
    }


}
