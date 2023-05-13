package xiatian.novoline.cnm;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class IQBoosterFrame extends JFrame {
	CrashReport report;
	public IQBoosterFrame (CrashReport report) {
		this.report = report;
		ImageIcon imageIcon;
		try {
			imageIcon = new ImageIcon(ImageIO.read(Minecraft.class.getResourceAsStream("/assets/minecraft/liquidwing/yellow_bean.png")));
			this.setIconImage(imageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
		} catch (IOException e) {
			e.printStackTrace();
		}
		initComponents();
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Display.destroy();
		this.setVisible(true);
	}

	public static void setClipboardString(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable trans = new StringSelection(text);
		clipboard.setContents(trans, null);
	}

	private void initComponents() {
		JLabel text = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		JTextArea log = new JTextArea();
		setTitle("L");
		Container contentPane = getContentPane();

		text.setText("我操你妈逼你崩溃之后能不能不要就截一个启动器崩溃的图，把错误日志全部复制下来操你妈.");

		log.setText(report.getCompleteReport());

		{
			scrollPane1.setViewportView(log);
		}

		JButton button = new JButton("点我一键复制日志");
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed (ActionEvent e) {
				setClipboardString(report.getCompleteReport());
				JOptionPane.showMessageDialog(null,"已复制日志","开香槟",JOptionPane.INFORMATION_MESSAGE);
			}
		});

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(contentPaneLayout.createParallelGroup()
										.addComponent(text, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
										.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
										.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
												.addGap(0, 509, Short.MAX_VALUE)
												.addComponent(button)))
								.addContainerGap())
		);
		contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(text, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(button)
								.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());
	}
}
