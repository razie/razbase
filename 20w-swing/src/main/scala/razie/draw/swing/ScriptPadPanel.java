/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.draw.swing;

import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.actions.ActionUtils;
import jsyntaxpane.actions.CaretMonitor;

/** mostly copy paste from jsyntaxpane.SyntaxTester */
public class ScriptPadPanel extends JPanel {
      /** Creates new form Tester */
  
   String ic="";
   
      public ScriptPadPanel(String initialContents, int rows, int cols) {
          ic=initialContents; 
      }
      
   // TODO use rows/cols
      public ScriptPadPanel init() {
          
         // this is a test for adding regex lexer.  It wont work unless the
         // JavaRegex.properties is found in the classpath
         // DefaultSyntaxKit.registerContentType("text/aa_regex", "jsyntaxpane.JavaRegexKit");
         initComponents();
         jCmbLangs.setModel(new DefaultComboBoxModel(DefaultSyntaxKit.getContentTypes()));
         jEdtTest.setContentType("text/scala");
         jEdtTest.setContentType(jCmbLangs.getItemAt(0).toString());
         jCmbLangs.setSelectedItem("text/scala");
         new CaretMonitor(jEdtTest, lblCaretPos);
         return this;
      }

      public String text () {  return jEdtTest.getSelectedText() == null ? jEdtTest.getText() : jEdtTest.getSelectedText();  }
      public String line () {  
//         Element e = jEdtTest.getDocument().getDefaultRootElement().getElement(curRow()-1);
//         String s = jEdtTest.getDocument().getDefaultRootElement().getElement(curRow()).;
//         return e.
         SyntaxDocument sDoc = ActionUtils.getSyntaxDocument(jEdtTest);
         if (sDoc != null) {
            String t;
            try {
               t = sDoc.getLineAt(jEdtTest.getCaretPosition());
            } catch (BadLocationException e) {
               // TODO Auto-generated catch block
               return null;
            }
            return t;
         }
         return null;
      }

      public int curRow () { return getLineAtCaret (jEdtTest); }
      public int curCol () { return getColumnAtCaret (jEdtTest); }
      
      public static int getColumnAtCaret(JTextComponent component)
      {
         int caretPosition = component.getCaretPosition();
         Element root = component.getDocument().getDefaultRootElement();
         int line = root.getElementIndex( caretPosition );
         int lineStart = root.getElement( line ).getStartOffset();
    
         return caretPosition - lineStart + 1;
   /*
         int rowStart = 0;
         try
         {
            rowStart = Utilities.getRowStart(component, component.getCaretPosition());
         }
         catch(BadLocationException ble) {}

         return component.getCaretPosition() - rowStart;
   */
      }
    
      /*
      **  Return the current line number at the Caret position.
      */
      public static int getLineAtCaret(JTextComponent component)
      {
         int caretPosition = component.getCaretPosition();
         Element root = component.getDocument().getDefaultRootElement();
    
         int i= root.getElementIndex( caretPosition ) + 1;
         return i;
      }
    
      /**
       * This method is called from within the constructor to
       * initialize the form.
       * WARNING: Do NOT modify this code. The content of this method is
       * always regenerated by the Form Editor.
       */
       // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
       private void initComponents() {

           lblCaretPos = new javax.swing.JLabel();
           jScrollPane1 = new javax.swing.JScrollPane();
           jEdtTest = new javax.swing.JEditorPane();
           lblToken = new javax.swing.JLabel();
           jCmbLangs = new javax.swing.JComboBox();

//           setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//           setTitle("JSyntaxPane Tester");

           lblCaretPos.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
           lblCaretPos.setText("Caret Position");

           jEdtTest.setContentType("");
           jEdtTest.setFont(new java.awt.Font("Monospaced", 0, 13));
           jEdtTest.setCaretColor(new java.awt.Color(153, 204, 255));
           jEdtTest.addCaretListener(new javax.swing.event.CaretListener() {
               public void caretUpdate(javax.swing.event.CaretEvent evt) {
                   jEdtTestCaretUpdate(evt);
               }
           });
           jScrollPane1.setViewportView(jEdtTest);

           lblToken.setFont(new java.awt.Font("Courier New", 0, 12));
           lblToken.setText("Token under cursor");

           jCmbLangs.setMaximumRowCount(20);
           jCmbLangs.setFocusable(false);
           jCmbLangs.addItemListener(new java.awt.event.ItemListener() {
               public void itemStateChanged(java.awt.event.ItemEvent evt) {
                   jCmbLangsItemStateChanged(evt);
               }
           });
           
           jToolBar1 = new javax.swing.JToolBar();

           jToolBar1.setRollover(true);
           jToolBar1.setFocusable(false);

          layout = new javax.swing.GroupLayout(this);

           this.setLayout(layout);
           layout.setHorizontalGroup(
               layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
               .addComponent(jScrollPane1)
               .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                   .addContainerGap()
                   .addComponent(jCmbLangs, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 500, Short.MAX_VALUE)
                   .addComponent(lblCaretPos, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                   .addContainerGap())
               .addGroup(layout.createSequentialGroup()
                   .addContainerGap()
                   .addComponent(lblToken, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                   .addGap(200, 200, 200))
           );
           layout.setVerticalGroup(
               layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                   .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                   .addGap(0, 0, 0)
                   .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                   .addComponent(lblToken, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                   .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                   .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                       .addComponent(lblCaretPos, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                       .addComponent(jCmbLangs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                   .addContainerGap())
           );

//           pack();
       }// </editor-fold>//GEN-END:initComponents

       // Variables declaration - do not modify//GEN-BEGIN:variables
       private javax.swing.JComboBox jCmbLangs;
       private javax.swing.JEditorPane jEdtTest;
       private javax.swing.JScrollPane jScrollPane1;
       private javax.swing.JToolBar jToolBar1;
       private javax.swing.JLabel lblCaretPos;
       private javax.swing.JLabel lblToken;
           javax.swing.GroupLayout layout;
       // End of variables declaration//GEN-END:variables

      private void jEdtTestCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jEdtTestCaretUpdate
              
      SyntaxDocument sDoc = ActionUtils.getSyntaxDocument(jEdtTest);
      
      if (sDoc != null) {
         Token t = sDoc.getTokenAt(evt.getDot());
         if (t != null) {
            CharSequence tData = t.getText(sDoc);
            if (t.length > 40) {
               tData = tData.subSequence(0, 40);
            }
            lblToken.setText(t.toString() + ": " + tData);
         } else {
            // null token, remove the status
            lblToken.setText("NO Token at cursor");
         }
      }

    }//GEN-LAST:event_jEdtTestCaretUpdate

    private void jCmbLangsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCmbLangsItemStateChanged
      if (evt.getStateChange() == ItemEvent.SELECTED) {
         String lang = jCmbLangs.getSelectedItem().toString();

         // save the state of the current JEditorPane, as it's Document is about
         // to be replaced.
         String oldText = jEdtTest.getText();

         // install a new DefaultSyntaxKit on the JEditorPane for the requested language.
         jEdtTest.setContentType(lang);
         // Recreate the Toolbar
         jToolBar1.removeAll();
         EditorKit kit = jEdtTest.getEditorKit();
         if (kit instanceof DefaultSyntaxKit) {
            DefaultSyntaxKit defaultSyntaxKit = (DefaultSyntaxKit) kit;
            defaultSyntaxKit.addToolBarActions(jEdtTest, jToolBar1);
            
            defaultSyntaxKit.addActions(jEdtTest);
         }
         jToolBar1.validate();
         try {
            // setText should not be called (read the JavaDocs).  Better use the read
            // method and create a new document.
            jEdtTest.read(new StringReader(oldText), lang);
         } catch (IOException ex) {
            Logger.getLogger(ScriptPadPanel.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      jEdtTest.requestFocusInWindow();
    }//GEN-LAST:event_jCmbLangsItemStateChanged

   }
