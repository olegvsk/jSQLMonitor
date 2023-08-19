package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import static javax.swing.KeyStroke.getKeyStroke;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.JList;
import javax.swing.JComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MainForm extends javax.swing.JFrame {

    private class SrvListCellRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(JList aSrvList,Object value,int index,boolean isSelected,boolean cellHasFocus) {
            Server server=(Server)value;
            setText(server.name);
           
            if (isSelected) {
                setBackground(aSrvList.getSelectionBackground());
                setForeground(aSrvList.getSelectionForeground());
            } 
            else {
                setBackground(aSrvList.getBackground());
                setForeground(aSrvList.getForeground());
            }
            
            setIcon(srvIcons[server.state.ordinal()]);
            return this;
        }
    }

    private class SrvListTransferHandler extends javax.swing.TransferHandler {
        private int Idx1,Idx2;
        
        @Override
        public int getSourceActions(JComponent aComp) {
            return LINK;
        }
       
        @Override
        public java.awt.datatransfer.Transferable createTransferable(JComponent aComp) {
            Idx1=srvList.getSelectedIndex();
            return new ServerTransferable(srvList.getSelectedValue());
        }

        @Override
        public void exportDone(JComponent aComp,java.awt.datatransfer.Transferable aServerTransferable,int aAction) {
            if (aAction==LINK)
            try {
                    srvListModel.removeElementAt(Idx2);
                    srvList.setSelectedIndex(Idx1);
                    XMLProc.saveConnections();
            }
            catch (Exception exc) {}
        }
 
        @Override
        public boolean canImport(javax.swing.TransferHandler.TransferSupport aSupport) {
            return aSupport.isDataFlavorSupported(ServerTransferable.SERVER_FLAVOR);
        }

        @Override
        public boolean importData(javax.swing.TransferHandler.TransferSupport aSupport) {
            try {
                Server server=(Server)aSupport.getTransferable().getTransferData(ServerTransferable.SERVER_FLAVOR);
                int lcIdx=((JList.DropLocation)aSupport.getDropLocation()).getIndex();
                srvListModel.add(lcIdx,server);
                Idx2=Idx1<lcIdx?Idx1:Idx1+1;
                Idx1=Idx2<lcIdx?lcIdx-1:lcIdx;
                return true;
            } 
            catch (Exception exc) {}

            return false;
        }        
    }
    
    private class TableHeaderMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            JTableHeader header=(JTableHeader)evt.getSource();
            if (javax.swing.SwingUtilities.isRightMouseButton(evt) & canManageColumns()) tableHeaderMenu.show(header,evt.getX(),evt.getY());
        }
    }
    
    private class TableMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
                JTable lcTable=(JTable)evt.getSource();
                lcTable.changeSelection(lcTable.rowAtPoint(evt.getPoint()),lcTable.columnAtPoint(evt.getPoint()),false,false);
            }
            if (javax.swing.SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount()==2) {
                showViewCell();
            }
        }
        @Override
        public void mouseReleased(MouseEvent evt) {
            if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
                JTable lcTable=(JTable)evt.getSource();
                lcTable.changeSelection(lcTable.rowAtPoint(evt.getPoint()),lcTable.columnAtPoint(evt.getPoint()),false,false);
            }
        }
    }
    
    private class MonitorSorterListener implements javax.swing.event.RowSorterListener {   
        @Override
        public void sorterChanged(javax.swing.event.RowSorterEvent evt) {
            if (evt.getSource().getSortKeys().size()!=0)
                curServer.sortColumn=(evt
                                     .getSource()
                                     .getSortKeys()
                                     .get(0)
                                     .getColumn()+1)*(evt
                                                     .getSource()
                                                     .getSortKeys()
                                                     .get(0)
                                                     .getSortOrder()==javax.swing.SortOrder.ASCENDING?1:-1);
        }
    }
    
    private class SrvListMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
                srvList.setSelectedIndex(srvList.locationToIndex(evt.getPoint()));
            }
            if (javax.swing.SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount()==2) {
                actSrvConnect.actionPerformed(new java.awt.event.ActionEvent(evt,0,""));
            }
        }
        @Override
        public void mouseReleased(MouseEvent evt) {
            if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
                srvList.setSelectedIndex(srvList.locationToIndex(evt.getPoint()));
            }
        }
    }
    
    private class SrvListSelectionListener implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            tuneActions();
            monitoring(getCurrentServer());
        }
    }
    
    private class TableKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode()==KeyEvent.VK_C && (evt.getModifiersEx() & (KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK))==KeyEvent.CTRL_DOWN_MASK) {
                copyCell();
                evt.consume();
            }
            if (evt.getKeyCode()==KeyEvent.VK_X && (evt.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK)==KeyEvent.CTRL_DOWN_MASK) {
                setFilterByCell();
                evt.consume();
            }
            if (evt.getKeyCode()==KeyEvent.VK_ENTER) {
                showViewCell();
                evt.consume();
            }
        }

        @Override
        public void keyTyped(KeyEvent evt) {}

        @Override
        public void keyReleased(KeyEvent evt) {}
    }
    
    private class MainKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode()==KeyEvent.VK_F5) tbRefresh.doClick();
            if (evt.getKeyCode()==KeyEvent.VK_P) tbPause.doClick();
            if (evt.getKeyCode()==KeyEvent.VK_F3) tbColors.doClick();
            if (evt.getKeyCode()==KeyEvent.VK_F2) hideSrvPanel();
        }

        @Override
        public void keyTyped(KeyEvent evt) {}

        @Override
        public void keyReleased(KeyEvent evt) {}
    }
    
    private class AuxRadioButtonMenuItem extends javax.swing.JRadioButtonMenuItem {
        int idx;
        
        AuxRadioButtonMenuItem(String text) {
            super(text);
        }
    }
    
    
    private ActSrvNew      actSrvNew;
    private ActSrvEdit     actSrvEdit;
    private ActSrvDelete   actSrvDelete;
    private ActConnect     actSrvConnect;
    private ActDisconnect  actSrvDisconnect;    
    private JList<Server>  srvList;
    private JTable         monitorTable;
    private PeriodComboBox cbPeriod;
    private ImageIcon      srvIcons[];
    private Worker         worker;
    private Table          table;
    private TableDataModel dataModel;
    private boolean        flError;
    private Server         curServer;
    private boolean        flFilterListUpdating;
    private LaFList        lafList;
    private int            curLaFIdx;
    private ColorsPanel    colorsPanel;
    SrvListModel<Server>   srvListModel;
    Settings               settings;
     
    
    Server getCurrentServer() {return srvList.getSelectedValue();} 
    int getCurrentIndex() {return srvList.getSelectedIndex();}           
    int getColumnCount() {return monitorTable.getColumnCount();}
    String getColumnName(int col) {return monitorTable.getColumnName(col);}
    boolean isVisibleColumn(int col) {return monitorTable.getColumnModel().getColumn(col).getWidth()!=0;}
    private boolean canManageColumns() {return curServer!=null && !flError && getColumnCount()!=0;}
    String getCurrentLaFName() {return lafList.getLaFName(curLaFIdx);}
    
    void setFirstSelected() {
        if (srvListModel.getSize()!=0) srvList.setSelectedIndex(0); else tuneActions();
    }

    void setLastSelected() {
        if (srvListModel.getSize()!=0) srvList.setSelectedIndex(srvListModel.getSize()-1); else tuneActions();
    }

    private void loadSrvImages() {
        srvIcons=new ImageIcon[4];        
        srvIcons[0]=getIcon("bled.png");
        srvIcons[1]=getIcon("gled.png");
        srvIcons[2]=getIcon("rled.png");
        srvIcons[3]=getIcon("yled.png");
    }
    
    private void setInitLaf() {
        javax.swing.UIManager.put("swing.boldMetal",settings.fbold);
        curLaFIdx=lafList.getLafIdx(settings.theme);
        if (curLaFIdx<0) curLaFIdx=0;
        if (curLaFIdx!=0) if (!setLaF(curLaFIdx)) curLaFIdx=0;
    }
    
    private void setLaFCorrection() {
        if (monitorTable==null) return;
        
        monitorToolBar.setLayout(new javax.swing.BoxLayout(monitorToolBar,javax.swing.BoxLayout.X_AXIS));        
        mainSplitPane.setDividerLocation(-1);        
        monitorTable.getTableHeader().setFont(new java.awt.Font(monitorTable.getFont().getName(),java.awt.Font.BOLD,monitorTable.getFont().getSize()));
        monitorTable.setIntercellSpacing(new java.awt.Dimension(1,1));
        monitorTable.setGridColor(java.awt.Color.GRAY);
        monitorTable.setShowGrid(true);
    }
    
    boolean setLaF(int idx) {
        boolean result=true;
        
        try {
            if ("Ocean".equals(lafList.getLaFTheme(idx))) javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.OceanTheme());
            if ("Steel".equals(lafList.getLaFTheme(idx))) javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
            javax.swing.UIManager.setLookAndFeel(lafList.getLaFClassName(idx));
        }
        catch(Exception exc) {
            result=false;
            Utils.ShowError("Unable to set UI Theme \""+lafList.getLaFName(idx)+"\". Exception: \n"+exc.getClass());
        }
        
        javax.swing.SwingUtilities.updateComponentTreeUI(this);
        setLaFCorrection();
    
        return result;
    }

    private void createThemeMenu() {
        javax.swing.ButtonGroup bg=new javax.swing.ButtonGroup();
        for (int i=0;i<lafList.getLaFsCount();i++) {
            AuxRadioButtonMenuItem mn=new AuxRadioButtonMenuItem(lafList.getLaFName(i));
            bg.add(mnThemes.add(mn));
            mn.idx=i;
            mn.setSelected(curLaFIdx==i);
            mn.addActionListener(evt -> {
                if (!setLaF(mn.idx)) {
                    mnThemes.getItem(curLaFIdx).setSelected(true);
                } 
                else {
                    curLaFIdx=mn.idx;
                    mnFonts.setEnabled(lafList.getLaFTheme(mn.idx)!=null);                    
                }
            });
        }
        mnFonts.setEnabled(lafList.getLaFTheme(curLaFIdx)!=null); 
    }
    
    private void hideSrvPanel() {
        mainSplitPane.setDividerLocation(mainSplitPane.getDividerLocation()<=1 ? mainSplitPane.getLastDividerLocation() : 0);
    }
    
    void updateStatusBars() {
        Server server=getCurrentServer();
        if (server==null)
        {
            txSrvStatus.setText("");
            txConnString.setText("");
        }
        else
        {
            txSrvStatus.setText(server.name);
            txConnProp.setText(server.getConnectionInfo());
            txConnString.setText(server.connString);
        }
    }
    
    void updateRecCount() {
        txRecCount.setText(canManageColumns() ? String.valueOf(monitorTable.getRowCount()) : "");
        txRecCount.setForeground(((javax.swing.table.TableRowSorter)monitorTable.getRowSorter()).getRowFilter()==null ? java.awt.Color.BLUE : java.awt.Color.MAGENTA);
    }
    
    void tuneActions()
    {
        Server server=getCurrentServer();
        if (server==null)
        {
            actSrvEdit.setEnabled(false);
            actSrvDelete.setEnabled(false);
            actSrvConnect.setEnabled(false);
            actSrvDisconnect.setEnabled(false); 
        }
        else
        {
            actSrvEdit.setEnabled(server.state!=ServerState.Connected);
            actSrvDelete.setEnabled(server.state!=ServerState.Connected);
            actSrvConnect.setEnabled(server.state!=ServerState.Connected && server.state!=ServerState.NoDriver);
            actSrvDisconnect.setEnabled(server.state==ServerState.Connected); 
        }
        updateStatusBars();
    }
    
    String getStrValue() {
        Object lcObj=null;
        if (monitorTable.getSelectedRow()!=-1 && monitorTable.getSelectedColumn()!=-1)
            lcObj=monitorTable.getValueAt(monitorTable.getSelectedRow(),monitorTable.getSelectedColumn());
        return lcObj==null ? "" : lcObj.toString();
    }
    
    void copyCell() {
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(getStrValue()),null);
    }
    
    void monitoring(Server aServer) {
        worker.setPause(true);
        
        long curTime=System.currentTimeMillis();
        while (!worker.waiting) {
            try {Thread.sleep(IDLE_TIME);}catch (InterruptedException exc) {}
            if (System.currentTimeMillis()-curTime>10000) {
                Utils.ShowError("Can't interrupt thread. Program will be closed.");
                System.exit(0);
            }
        }
        
        worker.setStatement(aServer==null || !aServer.connected() ? null : aServer.getStatement(),aServer);
        worker.setPause(false);
        tbPause.setSelected(false);
    }
    
    void updateMonitor(boolean updateHeader,boolean aError,Server aServer) {
        synchronized(worker.sync) {
            table.updateTable(worker.getBuffer(),updateHeader);
        }
        
        flError=aError;
        
        int lcRow=monitorTable.getSelectedRow();
        int lcCol=monitorTable.getSelectedColumn();
        
        if (updateHeader) {
            if (flError) 
                monitorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 
            else
            if (monitorTable.getAutoResizeMode()!=JTable.AUTO_RESIZE_OFF)
                monitorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            packSrvColumns();
            curServer=aServer;
            resetColumns();
            fillFilterFields();
            unpackSrvColumns();
            lcRow=-1;lcCol=-1;
        }
        
        dataModel.fireTableDataChanged();
        
        try {
            monitorTable.setRowSelectionInterval(lcRow,lcRow);
            monitorTable.setColumnSelectionInterval(lcCol,lcCol);
        } 
        catch(Exception exc) {}
        
        updateColorsStat();
        updateRecCount();
    }
    
    void showViewCell() {
        ViewCellForm viewCellForm=new ViewCellForm(mainForm,getColumnName(monitorTable.getSelectedColumn()),getStrValue());
        viewCellForm.setLocationRelativeTo(mainForm);
        viewCellForm.setSize(new java.awt.Dimension(settings.vcwidth,settings.vcheight));
        viewCellForm.setVisible(true);
    }
    
    ArrayList<ColorInfo> getColors() {
        final int nullColor=monitorTable.getBackground().getRGB();
        ArrayList result=new ArrayList<ColorInfo>();
        
        int[] colors=new int[monitorTable.getRowCount()];        
        for (int i=0;i<colors.length;i++) {
            Integer lcColor=table.getColor(monitorTable.convertRowIndexToModel(i));
            colors[i]=lcColor==null ? nullColor : lcColor;
        }
        java.util.Arrays.sort(colors);
        
        int lcColor=0;
        ColorInfo lcColorInfo=null;
        for (int i=0;i<colors.length;i++) {
            if (colors[i]!=lcColor || i==0) {
                lcColor=colors[i];
                lcColorInfo=new ColorInfo(lcColor);
                result.add(lcColorInfo);
            }
            else {
                lcColorInfo.inc();
            }
        }
        
        result.sort((java.util.Comparator<ColorInfo>)(c1,c2)->Integer.compare(c1.getNum(),c2.getNum())); 
        
        return result;
    }
    
    
    void updateColorsStat() {
        if (colorsPanel.isVisible()) colorsPanel.updateColors(getColors());
    }
    
    void fillFilterFields() {
        flFilterListUpdating=true;
        cbFilter.removeAllItems();
        if (canManageColumns()) {
            cbFilter.addItem("<all fields>");
            String[] columns=dataModel.getSortedColmnsList();
            for (String column:columns) cbFilter.addItem(column);
        }
        flFilterListUpdating=false;
        txFilter.setText("");
    }
    
    void setFilterByCell() {
        if (monitorTable.getSelectedColumn()==-1) return;
        int lcIdx=0;
        for (int i=1;i<cbFilter.getItemCount();i++) if (monitorTable.getColumnName(monitorTable.getSelectedColumn()).equals(cbFilter.getItemAt(i))) {
            lcIdx=i;
            break;
        }
        if (lcIdx>0) {
            txFilter.setText(getStrValue());
            cbFilter.setSelectedIndex(lcIdx);
            monitorTable.requestFocus();
        }
    }
    
    void applyFilter() {
        if (cbFilter.getSelectedIndex()<0 || txFilter.getText().length()==0) {
            ((javax.swing.table.TableRowSorter)monitorTable.getRowSorter()).setRowFilter(null);
        }
        else { 
            javax.swing.RowFilter<TableDataModel,Integer> filter;
            
            if (cbFilter.getSelectedIndex()==0)
                try {
                    filter=javax.swing.RowFilter.regexFilter(txFilter.getText());
                    ((javax.swing.table.TableRowSorter)monitorTable.getRowSorter()).setRowFilter(filter);
                }
                catch(Exception exc) {
                    Utils.ShowError(exc.getMessage());
                }
            else {
                int col=dataModel.getColumnByName(cbFilter.getSelectedItem().toString());
                if (col!=-1) {
                    try {
                        filter=javax.swing.RowFilter.regexFilter(txFilter.getText(),col);
                        ((javax.swing.table.TableRowSorter)monitorTable.getRowSorter()).setRowFilter(filter);
                    }
                    catch(Exception exc) {
                        Utils.ShowError(exc.getMessage());
                    }
                }
            }
        }
        updateColorsStat();
        updateRecCount();
    }
        
    private void adjustColumnsWidth() {
        for (int col=0;col<monitorTable.getColumnModel().getColumnCount();col++) {
            int maxWidth=monitorTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(monitorTable,monitorTable.getColumnName(col),false,false,-1,col).getPreferredSize().width;            
            for (int row=0;row<monitorTable.getRowCount();row++) maxWidth=Math.max(monitorTable.prepareRenderer(monitorTable.getCellRenderer(row,col),row,col).getPreferredSize().width,maxWidth);
            maxWidth=Math.min(maxWidth,MAX_COL_WIDTH);
            monitorTable.getColumnModel().getColumn(col).setPreferredWidth(maxWidth+monitorTable.getIntercellSpacing().width+4);
        }
    }
    
    void showColumn(int col,boolean show) {
        if (show) {
            monitorTable.getColumnModel().getColumn(col).setMinWidth(0);
            monitorTable.getColumnModel().getColumn(col).setMaxWidth(Integer.MAX_VALUE);
            monitorTable.getColumnModel().getColumn(col).setPreferredWidth((Integer)monitorTable.getColumnModel().getColumn(col).getIdentifier());
        }
        else {
            monitorTable.getColumnModel().getColumn(col).setIdentifier(monitorTable.getColumnModel().getColumn(col).getWidth());
            monitorTable.getColumnModel().getColumn(col).setMinWidth(0);
            monitorTable.getColumnModel().getColumn(col).setMaxWidth(0);
            monitorTable.getColumnModel().getColumn(col).setWidth(0);
        }
    }  
    
    private void resetColumns() {
        dataModel.fireTableStructureChanged();        
        for (int i=0;i<monitorTable.getColumnCount();i++) monitorTable.getColumnModel().getColumn(i).setCellRenderer(TableRenderer.renderer);
    }
    
    private void packSrvColumns() {
        if (!canManageColumns()) return;
        
        curServer.fltCol=cbFilter.getSelectedIndex();
        curServer.fltText=txFilter.getText();

        if (getColumnCount()!=curServer.columns.size()) {
            curServer.columns.clear();
            for (int i=0;i<getColumnCount();i++) {
                ArrayList<Integer> pair=new ArrayList(2);
                pair.add(monitorTable.getColumnModel().getColumn(i).getWidth());
                pair.add(monitorTable.getColumnModel().getColumn(i).getModelIndex());
                curServer.columns.add(pair);
            }
        }
        else {
            for (int i=0;i<getColumnCount();i++) {
                int width=monitorTable.getColumnModel().getColumn(i).getWidth();
                if (width==0) width=-(Integer)monitorTable.getColumnModel().getColumn(i).getIdentifier();
                curServer.columns.get(i).set(0,width);
                curServer.columns.get(i).set(1,monitorTable.getColumnModel().getColumn(i).getModelIndex());
            }
        }
    }
    
    private void unpackSrvColumns() {
        if (!canManageColumns()) return;
        
        txFilter.setText("");
        flFilterListUpdating=true;
        cbFilter.setSelectedIndex(0);
        if (cbFilter.getItemCount()>curServer.fltCol && curServer.fltCol!=-1) {
            cbFilter.setSelectedIndex(curServer.fltCol);
            if (curServer.fltText.length()!=0) {
                txFilter.setText(curServer.fltText);
                applyFilter();
            }
        }
        flFilterListUpdating=false;
        
        if (getColumnCount()!=curServer.columns.size()) {
            packSrvColumns();
            return;
        }
        
        for (int i=0;i<getColumnCount();i++) {
            int width=curServer.columns.get(i).get(0);
            
            if (width>0) {
                monitorTable.getColumnModel().getColumn(i).setPreferredWidth(width);
            }
            else {
                monitorTable.getColumnModel().getColumn(i).setIdentifier(-width);
                monitorTable.getColumnModel().getColumn(i).setMinWidth(0);
                monitorTable.getColumnModel().getColumn(i).setMaxWidth(0);
                monitorTable.getColumnModel().getColumn(i).setWidth(0);
            }
            
            int modelIndex=curServer.columns.get(i).get(1);
            monitorTable.getColumnModel().getColumn(i).setModelIndex(modelIndex);
            monitorTable.getColumnModel().getColumn(i).setHeaderValue(table.getColumnName(modelIndex));
            
            if (Math.abs(curServer.sortColumn)-1==modelIndex) {
                java.util.List<javax.swing.RowSorter.SortKey> keys=new ArrayList<javax.swing.RowSorter.SortKey>();
                keys.add(new javax.swing.RowSorter.SortKey(modelIndex,curServer.sortColumn>0 ? javax.swing.SortOrder.ASCENDING : javax.swing.SortOrder.DESCENDING));
                monitorTable.getRowSorter().setSortKeys(keys);
            }
        }
    }
    
    private void onClose() {
        packSrvColumns();
        XMLProc.saveConnections();
        settings.save();
        
        try {
            worker.setPause(false);
            worker.terminate();
            worker.join(1000);
        } 
        catch (InterruptedException exc) {}
    }
      
    public MainForm() {
        worker=new Worker();
        table=new Table();
        dataModel=new TableDataModel(table);
        lafList=new LaFList();
        settings=new Settings();
        setInitLaf();
     
        initComponents();
        
        monitorTable=new JTable();
        monitorTable.setModel(dataModel);
        monitorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        monitorTable.setAutoCreateRowSorter(true);
        monitorTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        monitorTable.getTableHeader().setForeground(new java.awt.Color(0,0,0x80));
        monitorTable.getTableHeader().setFont(new java.awt.Font(monitorTable.getFont().getName(),java.awt.Font.BOLD,monitorTable.getFont().getSize()));
        monitorTable.getTableHeader().addMouseListener(new TableHeaderMouseAdapter());
        monitorTable.setRowSorter(new javax.swing.table.TableRowSorter<>(dataModel));
        monitorTable.getRowSorter().addRowSorterListener(new MonitorSorterListener());
        monitorTable.addKeyListener(new MainKeyListener());
        monitorTable.addKeyListener(new TableKeyListener());
        monitorTable.addMouseListener(new TableMouseAdapter ());
        monitorTable.setComponentPopupMenu(tableMenu);     
        monitorScrollPane.setViewportView(monitorTable);

        srvListModel=new SrvListModel<>();
        srvList=new SrvList<>();
        srvList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        srvList.setModel(srvListModel);
        srvList.setCellRenderer(new SrvListCellRenderer());
        srvList.setComponentPopupMenu(srvMenu);
        srvList.setDragEnabled(true);
        srvList.setDropMode(javax.swing.DropMode.INSERT);
        srvList.setTransferHandler(new SrvListTransferHandler());
        srvList.setFixedCellHeight(22);
        srvList.getSelectionModel().addListSelectionListener(new SrvListSelectionListener());
        srvList.addMouseListener(new SrvListMouseAdapter()); 
        srvList.addKeyListener(new MainKeyListener());
        srvScrollPane.setViewportView(srvList);     
        
        colorsPanel=new ColorsPanel();
        monitorPanel.add(colorsPanel,java.awt.BorderLayout.EAST);
        colorsPanel.setVisible(false);
        
        actSrvNew=new ActSrvNew();
        btnSrvNew.setAction(actSrvNew);
        btnSrvNew.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_D,KeyEvent.CTRL_DOWN_MASK),0);
        btnSrvNew.getActionMap().put(0,btnSrvNew.getAction());        
        mnSrvNew.setAction(actSrvNew);
        mnSrvNew.setToolTipText(null);
                  
        actSrvEdit=new ActSrvEdit();
        btnSrvEdit.setAction(actSrvEdit);
        btnSrvEdit.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_ENTER,KeyEvent.ALT_DOWN_MASK),0);
        btnSrvEdit.getActionMap().put(0,btnSrvEdit.getAction());        
        mnSrvEdit.setAction(actSrvEdit);
        mnSrvEdit.setToolTipText(null);

        actSrvDelete=new ActSrvDelete();
        btnSrvDelete.setAction(actSrvDelete);
        btnSrvDelete.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_DELETE,0),0);
        btnSrvDelete.getActionMap().put(0,btnSrvDelete.getAction());        
        mnSrvDelete.setAction(actSrvDelete);
        mnSrvDelete.setToolTipText(null);
        
        actSrvConnect=new ActConnect();
        btnConnect.setAction(actSrvConnect);
        mnConnect.setAction(actSrvConnect);
        mnConnect.setToolTipText(null);
        
        actSrvDisconnect=new ActDisconnect();
        btnDisconnect.setAction(actSrvDisconnect);
        mnDisconnect.setAction(actSrvDisconnect);
        mnDisconnect.setToolTipText(null);
        
        cbPeriod=new PeriodComboBox();
        monitorToolBar.add(cbPeriod,0);
        cbPeriod.setPreferredSize(new java.awt.Dimension(120, 28));
        cbPeriod.addActionListener(evt->worker.setPeriod(cbPeriod.getValue()));
        cbPeriod.addKeyListener(new MainKeyListener());
        
        tbRefresh.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_F5,0),0);
        tbPause.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_P,0),0);
        tbColors.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_F3,0),0);

        cbFilter.addKeyListener(new MainKeyListener());
        txFilter.getDocument().addDocumentListener((ShortDocumentListener)()->applyFilter());
        txFilter.addKeyListener(new MainKeyListener());
        txConnProp.addKeyListener(new MainKeyListener());
        txConnString.addKeyListener(new MainKeyListener());
        mnFontBold.setSelected(settings.fbold);
        createThemeMenu();
        TableRenderer.renderer.setDTFormat(settings.dtformat);
        if (TableRenderer.renderer.getDTFormat()==0) mnDTFormat0.setSelected(true); else mnDTFormat1.setSelected(true);

        tbRefresh.setIcon(getIcon("refresh.png"));
        tbPause.setIcon(getIcon("pause.png"));       
        tbExit.setIcon(getIcon("exit.png"));
        tbColors.setIcon(getIcon("colors.png"));
        mnThemes.setIcon(getIcon("themes.png"));
        mnFonts.setIcon(getIcon("fonts.png"));
        mnHideSrvPanel.setIcon(getIcon("hide.png"));
        loadSrvImages();
                
        setLaFCorrection();

        javax.swing.ImageIcon lcIcon=getIcon("icon.png");
        if (lcIcon!=null) setIconImage(lcIcon.getImage());

        if (settings.width>200 && settings.height>100) setSize(new java.awt.Dimension(settings.width,settings.height));
        if ("Nimbus".equals(getCurrentLaFName())) javax.swing.SwingUtilities.updateComponentTreeUI(srvToolBar);
        mainSplitPane.setDividerLocation(srvToolBar.getPreferredSize().width+1);
        setLocationByPlatform(true);

        worker.setPeriod(cbPeriod.getValue());
        worker.start();        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        srvMenu = new javax.swing.JPopupMenu();
        mnConnect = new javax.swing.JMenuItem();
        mnDisconnect = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnSrvNew = new javax.swing.JMenuItem();
        mnSrvEdit = new javax.swing.JMenuItem();
        mnSrvDelete = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mnHideSrvPanel = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mnThemes = new javax.swing.JMenu();
        mnFonts = new javax.swing.JMenu();
        mnFontBold = new javax.swing.JCheckBoxMenuItem();
        tableHeaderMenu = new javax.swing.JPopupMenu();
        mnSelectColumns = new javax.swing.JMenuItem();
        mnAdjustColumns = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnRestColumns = new javax.swing.JMenuItem();
        tableMenu = new javax.swing.JPopupMenu();
        mnCopy = new javax.swing.JMenuItem();
        mnFilter = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnDTFormat = new javax.swing.JMenu();
        mnDTFormat0 = new javax.swing.JRadioButtonMenuItem();
        mnDTFormat1 = new javax.swing.JRadioButtonMenuItem();
        bgDTFormat = new javax.swing.ButtonGroup();
        mainSplitPane = new javax.swing.JSplitPane();
        srvPanel = new javax.swing.JPanel();
        srvToolBar = new javax.swing.JToolBar();
        btnConnect = new javax.swing.JButton();
        btnDisconnect = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnSrvNew = new javax.swing.JButton();
        btnSrvEdit = new javax.swing.JButton();
        btnSrvDelete = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        tbExit = new javax.swing.JButton();
        srvStatusBar = new javax.swing.JPanel();
        txSrvStatus = new javax.swing.JTextField();
        srvScrollPane = new javax.swing.JScrollPane();
        monitorPanel = new javax.swing.JPanel();
        monitorToolBar = new javax.swing.JToolBar();
        tbRefresh = new javax.swing.JButton();
        tbPause = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        tbColors = new javax.swing.JToggleButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        cbFilter = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txFilter = new javax.swing.JTextField();
        btnDelFilter = new javax.swing.JButton();
        monitorScrollPane = new javax.swing.JScrollPane();
        monitorStatusBar = new javax.swing.JPanel();
        txConnString = new javax.swing.JTextField();
        txRecCount = new javax.swing.JTextField();
        txConnProp = new javax.swing.JTextField();

        mnConnect.setText("jMenuItem1");
        srvMenu.add(mnConnect);

        mnDisconnect.setText("jMenuItem1");
        srvMenu.add(mnDisconnect);
        srvMenu.add(jSeparator2);

        mnSrvNew.setText("jMenuItem1");
        srvMenu.add(mnSrvNew);

        mnSrvEdit.setText("jMenuItem1");
        srvMenu.add(mnSrvEdit);

        mnSrvDelete.setText("jMenuItem1");
        srvMenu.add(mnSrvDelete);
        srvMenu.add(jSeparator5);

        mnHideSrvPanel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        mnHideSrvPanel.setText("Hide/Show panel");
        mnHideSrvPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnHideSrvPanelActionPerformed(evt);
            }
        });
        srvMenu.add(mnHideSrvPanel);
        srvMenu.add(jSeparator7);

        mnThemes.setText("UI Theme");
        srvMenu.add(mnThemes);

        mnFonts.setText("UI Fonts");

        mnFontBold.setText("UI Fonts Bold");
        mnFontBold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnFontBoldActionPerformed(evt);
            }
        });
        mnFonts.add(mnFontBold);

        srvMenu.add(mnFonts);

        mnSelectColumns.setText("Select columns");
        mnSelectColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSelectColumnsActionPerformed(evt);
            }
        });
        tableHeaderMenu.add(mnSelectColumns);

        mnAdjustColumns.setText("Adjust columns width");
        mnAdjustColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnAdjustColumnsActionPerformed(evt);
            }
        });
        tableHeaderMenu.add(mnAdjustColumns);
        tableHeaderMenu.add(jSeparator4);

        mnRestColumns.setText("Reset columns settings");
        mnRestColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnRestColumnsActionPerformed(evt);
            }
        });
        tableHeaderMenu.add(mnRestColumns);

        mnCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnCopy.setText("Copy value");
        mnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCopyActionPerformed(evt);
            }
        });
        tableMenu.add(mnCopy);

        mnFilter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnFilter.setText("Filter by value");
        mnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnFilterActionPerformed(evt);
            }
        });
        tableMenu.add(mnFilter);
        tableMenu.add(jSeparator9);

        mnDTFormat.setText("Timestamp format");

        bgDTFormat.add(mnDTFormat0);
        mnDTFormat0.setText("default");
        mnDTFormat0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnDTFormat0ActionPerformed(evt);
            }
        });
        mnDTFormat.add(mnDTFormat0);

        bgDTFormat.add(mnDTFormat1);
        mnDTFormat1.setText("dd.MM.yyyy HH:mm:ss");
        mnDTFormat1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnDTFormat1ActionPerformed(evt);
            }
        });
        mnDTFormat.add(mnDTFormat1);

        tableMenu.add(mnDTFormat);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jSQLMonitor");
        setSize(new java.awt.Dimension(0, 0));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainSplitPane.setDividerSize(6);

        srvPanel.setLayout(new java.awt.BorderLayout());

        srvToolBar.setFloatable(false);

        btnConnect.setFocusable(false);
        btnConnect.setHideActionText(true);
        btnConnect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        srvToolBar.add(btnConnect);

        btnDisconnect.setFocusable(false);
        btnDisconnect.setHideActionText(true);
        btnDisconnect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        srvToolBar.add(btnDisconnect);
        srvToolBar.add(jSeparator1);

        btnSrvNew.setFocusable(false);
        btnSrvNew.setHideActionText(true);
        btnSrvNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        srvToolBar.add(btnSrvNew);

        btnSrvEdit.setFocusable(false);
        btnSrvEdit.setHideActionText(true);
        btnSrvEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        srvToolBar.add(btnSrvEdit);

        btnSrvDelete.setFocusable(false);
        btnSrvDelete.setHideActionText(true);
        btnSrvDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        srvToolBar.add(btnSrvDelete);
        srvToolBar.add(jSeparator6);

        tbExit.setToolTipText("Exit");
        tbExit.setFocusable(false);
        tbExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbExitActionPerformed(evt);
            }
        });
        srvToolBar.add(tbExit);

        srvPanel.add(srvToolBar, java.awt.BorderLayout.PAGE_START);

        srvStatusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 2, 2));

        txSrvStatus.setEditable(false);
        txSrvStatus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txSrvStatus.setFocusable(false);

        javax.swing.GroupLayout srvStatusBarLayout = new javax.swing.GroupLayout(srvStatusBar);
        srvStatusBar.setLayout(srvStatusBarLayout);
        srvStatusBarLayout.setHorizontalGroup(
            srvStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txSrvStatus)
        );
        srvStatusBarLayout.setVerticalGroup(
            srvStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txSrvStatus, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        srvPanel.add(srvStatusBar, java.awt.BorderLayout.PAGE_END);
        srvPanel.add(srvScrollPane, java.awt.BorderLayout.CENTER);

        mainSplitPane.setLeftComponent(srvPanel);

        monitorPanel.setLayout(new java.awt.BorderLayout());

        monitorToolBar.setFloatable(false);

        tbRefresh.setToolTipText("Refresh");
        tbRefresh.setFocusable(false);
        tbRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbRefreshActionPerformed(evt);
            }
        });
        monitorToolBar.add(tbRefresh);

        tbPause.setToolTipText("Pause");
        tbPause.setFocusable(false);
        tbPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbPauseActionPerformed(evt);
            }
        });
        monitorToolBar.add(tbPause);

        jSeparator3.setSeparatorSize(new java.awt.Dimension(20, 10));
        monitorToolBar.add(jSeparator3);

        tbColors.setToolTipText("Show color statistics");
        tbColors.setFocusable(false);
        tbColors.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbColors.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbColors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbColorsActionPerformed(evt);
            }
        });
        monitorToolBar.add(tbColors);

        jSeparator8.setSeparatorSize(new java.awt.Dimension(20, 10));
        monitorToolBar.add(jSeparator8);

        jLabel1.setText("Filter ");
        monitorToolBar.add(jLabel1);

        cbFilter.setMaximumRowCount(40);
        cbFilter.setPreferredSize(new java.awt.Dimension(200, 28));
        cbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFilterActionPerformed(evt);
            }
        });
        monitorToolBar.add(cbFilter);

        jLabel2.setText(" RegExp ");
        monitorToolBar.add(jLabel2);

        txFilter.setPreferredSize(new java.awt.Dimension(100, 23));
        txFilter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txFilterMouseClicked(evt);
            }
        });
        monitorToolBar.add(txFilter);

        btnDelFilter.setText("X");
        btnDelFilter.setFocusable(false);
        btnDelFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelFilterActionPerformed(evt);
            }
        });
        monitorToolBar.add(btnDelFilter);

        monitorPanel.add(monitorToolBar, java.awt.BorderLayout.PAGE_START);
        monitorPanel.add(monitorScrollPane, java.awt.BorderLayout.CENTER);

        monitorStatusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 2, 2));

        txConnString.setEditable(false);
        txConnString.setToolTipText("Connection string");
        txConnString.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        txRecCount.setEditable(false);
        txRecCount.setForeground(java.awt.Color.blue);
        txRecCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txRecCount.setToolTipText("Record count");
        txRecCount.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txRecCount.setFocusable(false);

        txConnProp.setEditable(false);
        txConnProp.setForeground(new java.awt.Color(0, 102, 0));
        txConnProp.setToolTipText("Connection info: user@catalog.schema");
        txConnProp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout monitorStatusBarLayout = new javax.swing.GroupLayout(monitorStatusBar);
        monitorStatusBar.setLayout(monitorStatusBarLayout);
        monitorStatusBarLayout.setHorizontalGroup(
            monitorStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monitorStatusBarLayout.createSequentialGroup()
                .addComponent(txRecCount, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txConnProp, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txConnString))
        );
        monitorStatusBarLayout.setVerticalGroup(
            monitorStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txRecCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, monitorStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txConnString, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txConnProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        monitorPanel.add(monitorStatusBar, java.awt.BorderLayout.PAGE_END);

        mainSplitPane.setRightComponent(monitorPanel);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 1016, 639);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        onClose();
    }//GEN-LAST:event_formWindowClosing

    private void tbRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbRefreshActionPerformed
        worker.forceUpdate();
    }//GEN-LAST:event_tbRefreshActionPerformed

    private void mnAdjustColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnAdjustColumnsActionPerformed
        adjustColumnsWidth();
    }//GEN-LAST:event_mnAdjustColumnsActionPerformed

    private void mnSelectColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSelectColumnsActionPerformed
        int monitorHeight=java.awt.MouseInfo.getPointerInfo().getDevice().getDisplayMode().getHeight();
        int taskbarHeight=java.awt.Toolkit.getDefaultToolkit().getScreenInsets(java.awt.MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration()).bottom;
        int mousePos=java.awt.MouseInfo.getPointerInfo().getLocation().y;
        
        ColumnsForm columnsForm=new ColumnsForm();
        columnsForm.setLocation(java.awt.MouseInfo.getPointerInfo().getLocation());        
        columnsForm.setSize(new java.awt.Dimension(columnsForm.getPreferredSize().width,Math.min(columnsForm.getPreferredSize().height,monitorHeight-mousePos-taskbarHeight)));
        columnsForm.setVisible(true);
    }//GEN-LAST:event_mnSelectColumnsActionPerformed

    private void tbPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbPauseActionPerformed
        worker.setPause(tbPause.isSelected());
    }//GEN-LAST:event_tbPauseActionPerformed

    private void mnRestColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnRestColumnsActionPerformed
        curServer.sortColumn=0;
        resetColumns();
    }//GEN-LAST:event_mnRestColumnsActionPerformed

    private void cbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterActionPerformed
        if (flFilterListUpdating) return;
        txFilter.selectAll();
        txFilter.requestFocus();
        applyFilter();
    }//GEN-LAST:event_cbFilterActionPerformed

    private void btnDelFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelFilterActionPerformed
        txFilter.setText("");
        applyFilter();
    }//GEN-LAST:event_btnDelFilterActionPerformed

    private void txFilterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txFilterMouseClicked
        if (javax.swing.SwingUtilities.isLeftMouseButton(evt) & evt.getClickCount()>1) {
            txFilter.setText("");
            applyFilter();
        }
    }//GEN-LAST:event_txFilterMouseClicked

    private void tbExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbExitActionPerformed
        onClose();
        dispose();
    }//GEN-LAST:event_tbExitActionPerformed

    private void mnFontBoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnFontBoldActionPerformed
        boolean fl=((javax.swing.JCheckBoxMenuItem)evt.getSource()).isSelected();
        javax.swing.UIManager.put("swing.boldMetal",fl);
        if (!setLaF(curLaFIdx)) ((javax.swing.JCheckBoxMenuItem)evt.getSource()).setSelected(!fl);
    }//GEN-LAST:event_mnFontBoldActionPerformed

    private void mnHideSrvPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnHideSrvPanelActionPerformed
        hideSrvPanel();
    }//GEN-LAST:event_mnHideSrvPanelActionPerformed

    private void tbColorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbColorsActionPerformed
        colorsPanel.setVisible(tbColors.isSelected());
        updateColorsStat();
    }//GEN-LAST:event_tbColorsActionPerformed

    private void mnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnCopyActionPerformed
        copyCell();
    }//GEN-LAST:event_mnCopyActionPerformed

    private void mnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnFilterActionPerformed
        setFilterByCell();
    }//GEN-LAST:event_mnFilterActionPerformed

    private void mnDTFormat0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnDTFormat0ActionPerformed
        TableRenderer.renderer.setDTFormat(0);
        monitorTable.repaint();
    }//GEN-LAST:event_mnDTFormat0ActionPerformed

    private void mnDTFormat1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnDTFormat1ActionPerformed
        TableRenderer.renderer.setDTFormat(1);
        monitorTable.repaint();
    }//GEN-LAST:event_mnDTFormat1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgDTFormat;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnDelFilter;
    private javax.swing.JButton btnDisconnect;
    private javax.swing.JButton btnSrvDelete;
    private javax.swing.JButton btnSrvEdit;
    private javax.swing.JButton btnSrvNew;
    private javax.swing.JComboBox<String> cbFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JMenuItem mnAdjustColumns;
    private javax.swing.JMenuItem mnConnect;
    private javax.swing.JMenuItem mnCopy;
    private javax.swing.JMenu mnDTFormat;
    private javax.swing.JRadioButtonMenuItem mnDTFormat0;
    private javax.swing.JRadioButtonMenuItem mnDTFormat1;
    private javax.swing.JMenuItem mnDisconnect;
    private javax.swing.JMenuItem mnFilter;
    private javax.swing.JCheckBoxMenuItem mnFontBold;
    private javax.swing.JMenu mnFonts;
    private javax.swing.JMenuItem mnHideSrvPanel;
    private javax.swing.JMenuItem mnRestColumns;
    private javax.swing.JMenuItem mnSelectColumns;
    private javax.swing.JMenuItem mnSrvDelete;
    private javax.swing.JMenuItem mnSrvEdit;
    private javax.swing.JMenuItem mnSrvNew;
    private javax.swing.JMenu mnThemes;
    private javax.swing.JPanel monitorPanel;
    private javax.swing.JScrollPane monitorScrollPane;
    private javax.swing.JPanel monitorStatusBar;
    private javax.swing.JToolBar monitorToolBar;
    private javax.swing.JPopupMenu srvMenu;
    private javax.swing.JPanel srvPanel;
    private javax.swing.JScrollPane srvScrollPane;
    private javax.swing.JPanel srvStatusBar;
    private javax.swing.JToolBar srvToolBar;
    private javax.swing.JPopupMenu tableHeaderMenu;
    private javax.swing.JPopupMenu tableMenu;
    private javax.swing.JToggleButton tbColors;
    private javax.swing.JButton tbExit;
    private javax.swing.JToggleButton tbPause;
    private javax.swing.JButton tbRefresh;
    private javax.swing.JTextField txConnProp;
    private javax.swing.JTextField txConnString;
    private javax.swing.JTextField txFilter;
    private javax.swing.JTextField txRecCount;
    private javax.swing.JTextField txSrvStatus;
    // End of variables declaration//GEN-END:variables
}
