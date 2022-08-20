package com.jeff87218.MasterDuelDeckExport.Gui;

import com.jeff87218.MasterDuelDeckExport.Util.MemoryManipulate;
import com.jeff87218.MasterDuelDeckExport.Writer.CardsWriter;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainForm extends JFrame {
    private JButton Button;
    private JPanel mainPanel;
    private JTextField textField1;
    private JButton dumpToClipboardButton;

    static WinNT.HANDLE handle;
    static int processId;
    static CardsWriter cardsWriter = new CardsWriter();

    public MainForm() {
        processId = MemoryManipulate.getProcessId("masterduel");
        if (processId == 0) {
            JOptionPane.showMessageDialog(this, "MasterDuel not found");
            System.exit(0);
        }
        handle = MemoryManipulate.getHANDLE(processId);
        setContentPane(mainPanel);
        setSize(500, 150);
        setTitle("Yugioh MasterDuel Deck Dumper");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);


        Button.addActionListener(e -> {
            List<Integer> mainDeckList = new ArrayList<>();
            int index = 0x0;
            Pointer mainDeck = MemoryManipulate.findDynAddressPointer(processId, handle, new int[]{0xB8, 0x10, 0xF8, 0x1E8, 0x150, 0x40, 0x20}, 0x01F690E8, "GameAssembly.dll");
            for (int i = 0; i < 60; i++) {
                Memory memory = MemoryManipulate.readMemory(handle, mainDeck.share(index), 4);
                int cardID = memory.getShort(0);
                memory.close();
                if (cardID == 0) {
                    break;
                }
                index += 0x18;
                mainDeckList.add(cardID);
            }

            List<Integer> extraDeckList = new ArrayList<>();
            index = 0x0;
            Pointer extraDeck = MemoryManipulate.findDynAddressPointer(processId, handle, new int[]{0xB8, 0x10, 0xF8, 0x1E8, 0x150, 0x10, 0x20}, 0x01F690E8, "GameAssembly.dll");
            for (int i = 0; i < 15; i++) {
                Memory memory = MemoryManipulate.readMemory(handle, extraDeck.share(index), 4);
                int cardID = memory.getShort(0);
                memory.close();
                if (cardID == 0) {
                    break;
                }
                index += 0x18;
                extraDeckList.add(cardID);
            }
            try {
                cardsWriter.writeCSV(textField1.getText(), mainDeckList, extraDeckList);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MemoryManipulate.closeHandle(handle);
            }
        });

        dumpToClipboardButton.addActionListener(e -> {
            List<Integer> mainDeckList = new ArrayList<>();
            int index = 0x0;
            Pointer mainDeck = MemoryManipulate.findDynAddressPointer(processId, handle, new int[]{0xB8, 0x10, 0xF8, 0x1E8, 0x150, 0x40, 0x20}, 0x01F690E8, "GameAssembly.dll");
            for (int i = 0; i < 60; i++) {
                Memory memory = MemoryManipulate.readMemory(handle, mainDeck.share(index), 4);
                int cardID = memory.getShort(0);
                memory.close();
                if (cardID == 0) {
                    break;
                }
                index += 0x18;
                mainDeckList.add(cardID);
            }

            List<Integer> extraDeckList = new ArrayList<>();
            index = 0x0;
            Pointer extraDeck = MemoryManipulate.findDynAddressPointer(processId, handle, new int[]{0xB8, 0x10, 0xF8, 0x1E8, 0x150, 0x10, 0x20}, 0x01F690E8, "GameAssembly.dll");
            for (int i = 0; i < 15; i++) {
                Memory memory = MemoryManipulate.readMemory(handle, extraDeck.share(index), 4);
                int cardID = memory.getShort(0);
                memory.close();
                if (cardID == 0) {
                    break;
                }
                index += 0x18;
                extraDeckList.add(cardID);
            }

            try {
                cardsWriter.Clipboard(mainDeckList, extraDeckList);
                JOptionPane.showMessageDialog(this, "Done!");

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
    }


}
