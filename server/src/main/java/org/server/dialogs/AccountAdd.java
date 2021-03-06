package org.server.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;

import org.common.accounts.Account;
import org.server.Server;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AccountAdd extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField accountNumber;
	private JFormattedTextField accountAmount, accountPassword;
	private JCheckBox accountStatus;
	private boolean editMode;
	private Account account;

	public AccountAdd() {
		init();
	}

	public AccountAdd(boolean mode, Account account) {
		editMode = mode;
		this.account = account;
		init();
	}	
	
	private void init() {
		addWindowListener(new WindowAdapter() {
            public void windowDeactivated(WindowEvent e) {
                Server.getAccounts().setBlocked(false);
                Server.updateData();
            }
		});
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle((!editMode) ? "Add new Account" : "Edit Account");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 224, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			accountNumber = new JTextField();
			accountNumber.setBounds(10, 31, 194, 20);
			if (editMode) {
				accountNumber.setText(account.getCardNumber());
				accountNumber.setEnabled(false);
			}
			contentPanel.add(accountNumber);
			accountNumber.setColumns(10);
		}
		
		JLabel lblNewLabel = new JLabel("Account number");
		lblNewLabel.setBounds(10, 11, 194, 14);
		contentPanel.add(lblNewLabel);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 62, 194, 14);
		contentPanel.add(lblPassword);
		
		accountPassword = new JFormattedTextField();
		accountPassword.setColumns(10);
		accountPassword.setBounds(10, 87, 194, 20);
		if (editMode) {
			accountPassword.setText(String.valueOf(account.getPassword()));
		}
		contentPanel.add(accountPassword);
		
		accountStatus = new JCheckBox("Enabled");
		accountStatus.setSelected(true);
		accountStatus.setBounds(10, 170, 194, 23);
		if (editMode) {
			accountStatus.setSelected(account.isStatus());
		}
		contentPanel.add(accountStatus);
		
		JLabel lblAccountAmount = new JLabel("Account amount");
		lblAccountAmount.setBounds(10, 118, 98, 14);
		contentPanel.add(lblAccountAmount);
		
		accountAmount = new JFormattedTextField();
		accountAmount.setText("0");
		accountAmount.setBounds(10, 143, 98, 20);
		if (editMode) {
			accountAmount.setText(String.valueOf(account.getAmount()));
		}
		contentPanel.add(accountAmount);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton((!editMode) ? "OK" : "Save");
				okButton.addActionListener(submit());
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						close();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}		
	}
	
	private ActionListener submit() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (accountNumber.getText().equals("")) {
					close();
					return;
				}
				if (accountPassword.getText().equals("")) {
					close();
					return;
				}				
				if (editMode) {
					account.setAmount(Double.parseDouble(accountAmount.getText()));
					account.setPassword(Integer.parseInt(accountPassword.getText()));
					account.setStatus(accountStatus.isSelected());
				} else {
					if (Server.getAccounts().isExisting(accountNumber.getText())) {
						JOptionPane.showMessageDialog(null,
							    "Account with such card numer already existed. Change please account number and try again.",
							    "Duplicate error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}					
					Account account = new Account();
					account.setAmount(Double.parseDouble(accountAmount.getText()));
					account.setCardNumber(accountNumber.getText());
					account.setPassword(Integer.parseInt(accountPassword.getText()));
					account.setStatus(accountStatus.isSelected());
					Server.getAccounts().addAccount(account);
				}
				close();
			}
		};
	}
	
	private void close() {
		dispose();
	}

	public void setEditMode(boolean mode) {
		editMode = mode;
	}
	
	public boolean getEditMode() {
		return editMode;
	}	
}
