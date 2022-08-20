package com.jeff87218.MasterDuelDeckExport.Util;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.List;

public class MemoryManipulate {
    static WinDef.HWND hwnd = null;
    static final Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);
    static final User32 user32 = Native.load("user32", User32.class);


    public static WinNT.HANDLE getHANDLE(int processID) {
        WinNT.HANDLE handle = null;
        int reTryCount = 0;
        while (handle == null) {
            if (reTryCount > 10) {
                throw new NullPointerException();
            }
            handle = kernel32.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ | WinNT.PROCESS_VM_WRITE | WinNT.PROCESS_VM_OPERATION, true, processID);
            reTryCount++;
        }
        return handle;
    }

    public static int getProcessId(String windowtitle) {
        IntByReference pid = new IntByReference(0);

        //掃描所有視窗
        user32.EnumWindows((hWnd, data) -> {
            char[] windowText = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, windowText, 512);
            String wText = Native.toString(windowText);
            if (wText.contains(windowtitle)) {
                hwnd = hWnd;
            }
            return true;
        }, null);

        user32.GetWindowThreadProcessId(hwnd, pid);

        return pid.getValue();
    }


    public static boolean writeMemoryByte(Pointer handle, Pointer address, byte[] Value) {

        Memory toWrite = new Memory(Value.length);
        for (int i = 0; i < Value.length; i++) {
            toWrite.setByte(i, Value[i]);
        }

        boolean b = kernel32.WriteProcessMemory(new WinNT.HANDLE(handle), address, toWrite, Value.length, null);
        toWrite.close();
        return b;
    }

    public static boolean writeMemoryByte(WinNT.HANDLE handle, Pointer address, byte[] Value) {
        return writeMemoryByte(handle.getPointer(), address, Value);
    }

    public static Memory readMemory(Pointer process, Pointer address, int bytesToRead) {
        IntByReference read = new IntByReference(0);
        Memory output = new Memory(bytesToRead);

        kernel32.ReadProcessMemory(new WinNT.HANDLE(process), address, output, bytesToRead, read);
        return output;
    }

    public static Memory readMemory(WinNT.HANDLE process, Pointer address, int bytesToRead) {
        return readMemory(process.getPointer(), address, bytesToRead);
    }


    public static Pointer findDynAddressPointer(int processId, WinNT.HANDLE process, int[] offsets, long baseAddress, String moduleName) {

        int size = 8;
        Memory pTemp = new Memory(size);
        Pointer baseAddPointer;
        int reTryCount = 0;
        Tlhelp32.MODULEENTRY32W moduleentry32W = null;

        while (moduleentry32W == null) {
            if (reTryCount > 10) {
                throw new NullPointerException();
            }
            List<Tlhelp32.MODULEENTRY32W> moduleentry32WS = Kernel32Util.getModules(processId);
            //掃描process下的所有Module
            for (int i = 0; i < moduleentry32WS.size(); i++) {
                if (moduleentry32WS.get(i).szModule().equals(moduleName)) {
                    //取得基址
                    moduleentry32W = moduleentry32WS.get(i);
                }
            }
            reTryCount++;
        }

        baseAddPointer = moduleentry32W.modBaseAddr.share(baseAddress);
        kernel32.ReadProcessMemory(process, baseAddPointer, pTemp, size, null);
        for (int i = 0; i < offsets.length - 1; i++) {
            kernel32.ReadProcessMemory(process, pTemp.getPointer(0).share(offsets[i]), pTemp, size, null);
        }

        return pTemp.getPointer(0).share(offsets[offsets.length - 1]);
    }

    public static void closeHandle(WinNT.HANDLE handle) {
        kernel32.CloseHandle(handle);
    }


}
