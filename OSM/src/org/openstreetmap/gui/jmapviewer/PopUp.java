package org.openstreetmap.gui.jmapviewer;
import java.awt.*;
    import java.awt.event.*;
    import javax.swing.*;

    public class PopUp extends JPopupMenu implements ActionListener
    {
      Component selectedComponent;

      public PopUp(  ) {
        this.setName("Color");
        this.add(makeMenuItem("Red"));
        this.add(makeMenuItem("Green"));
        this.add(makeMenuItem("Blue"));

        MouseListener mouseListener = new MouseAdapter(  ) {
          public void mousePressed(MouseEvent e) {  checkPopup(e);}
          public void mouseClicked(MouseEvent e) { checkPopup(e); }
          public void mouseReleased(MouseEvent e) { checkPopup(e);}
          private void checkPopup(MouseEvent e) {
            if (e.getButton() == e.BUTTON2_DOWN_MASK) {
              selectedComponent = e.getComponent(  );
              show(e.getComponent(  ), e.getX(  ), e.getY(  ));
            }
          }
        };

      }

      public void actionPerformed(ActionEvent e) {
        String color = e.getActionCommand(  );
        if (color.equals("Red"))
          System.out.println("red");
        else if (color.equals("Green"))
            System.out.println("green");
        else if (color.equals("Blue"))
            System.out.println("blue");
      }

      private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener( this );
        return item;
      }

      public static void main(String[] args) {
         new PopUp(  );
      }
    }
