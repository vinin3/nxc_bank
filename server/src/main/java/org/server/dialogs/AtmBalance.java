package org.server.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.common.atms.Atm;
import org.server.Server;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AtmBalance extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField atmBillName;
	private JTextField atmBillAmount;
	private JTable atmBillsTable;
	private JLabel atmBalanceAmountLabel;
	private Atm atm;

	public AtmBalance(Atm atm) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent event) {
				Server.getAtms().setBlocked(false);
			}
		});
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		this.atm = atm;
		setTitle("ATM Balance");
		setBounds(100, 100, 390, 374);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblBalanceAmount = new JLabel("Balance:");
		lblBalanceAmount.setBounds(10, 11, 102, 14);
		contentPanel.add(lblBalanceAmount);
		
		atmBalanceAmountLabel = new JLabel(Double.toString(this.atm.getBalance()));
		atmBalanceAmountLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		atmBalanceAmountLabel.setBounds(122, 11, 128, 14);
		contentPanel.add(atmBalanceAmountLabel);
		
		JLabel lblBillName = new JLabel("Bill name");
		lblBillName.setBounds(10, 37, 67, 14);
		contentPanel.add(lblBillName);
		
		atmBillName = new JTextField();
		atmBillName.setBounds(10, 61, 102, 20);
		contentPanel.add(atmBillName);
		atmBillName.setColumns(10);
		
		JLabel lblBillAmount = new JLabel("Bill amount");
		lblBillAmount.setBounds(122, 37, 128, 14);
		contentPanel.add(lblBillAmount);
		
		atmBillAmount = new JTextField();
		atmBillAmount.setColumns(10);
		atmBillAmount.setBounds(122, 61, 102, 20);
		contentPanel.add(atmBillAmount);
		
		JButton atmBtnAddBill = new JButton("Save");
		atmBtnAddBill.addMouseListener(atmSaveBill());
		atmBtnAddBill.setBounds(234, 60, 91, 21);
		contentPanel.add(atmBtnAddBill);
		
		JPanel atmBillsPanel = new JPanel();
		atmBillsPanel.setBounds(10, 92, 357, 200);
		contentPanel.add(atmBillsPanel);
		atmBillsPanel.setLayout(new BorderLayout(0, 0));
		
		String[] atmBillsColumnNames = {"Bill name", "Bill amount"};
		Object[][] atmBillsTableData = this.atm.getBillsData();
		atmBillsTable = new JTable(atmBillsTableData, atmBillsColumnNames);
		atmBillsTable.setModel(new DefaultTableModel(
			atmBillsTableData,
			new String[] {
				"Bill name", "Bill amount"
			}
		) {
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] {
				false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		atmBillsTable.getColumnModel().getColumn(0).setResizable(false);
		atmBillsTable.getColumnModel().getColumn(1).setResizable(false);
		atmBillsTable.setCellSelectionEnabled(true);
		atmBillsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane atmBillsScrollPane = new JScrollPane(atmBillsTable);
		atmBillsTable.setFillsViewportHeight(true);
		atmBillsPanel.add(atmBillsScrollPane);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			JButton btnRefresh = new JButton("Refresh");
			btnRefresh.addMouseListener(atmBillsRefresh());
			buttonPane.add(btnRefresh);
		}
	}

	private MouseAdapter atmBillsRefresh() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				refreshData();
			}
		};
	}

	private void refreshData() {
//		DefaultTableModel data = (DefaultTableModel) atmBillsTable.getModel();
//		data.setRowCount(0);
		atmBillsTable.setModel(new DefaultTableModel(
			atm.getBillsData(),
			new String[] {
				"Bill name", "Bill amount"
			}
		) {
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] {
				false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		atmBalanceAmountLabel.setText(Double.toString(atm.getBalance()));
	}
	
	private MouseAdapter atmSaveBill() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (atmBillName.getText().isEmpty() || atmBillName.getText().equals("0")) {
					return;
				}
				if (atmBillAmount.getText().isEmpty() || atmBillAmount.getText().equals("0")) {
					return;
				}
				atm.addBill(Integer.parseInt(atmBillName.getText()), Integer.parseInt(atmBillAmount.getText()));
				atmBillName.setText("");
				atmBillAmount.setText("");				
				refreshData();
			}
		};
	}
}
