package com.idc.explorer;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import com.idc.trace.LogHelper;

public class ExplorerDragSourceListener implements DragSourceListener, DragGestureListener {

	private ExplorerTree m_explorerTree;
	public ExplorerDragSourceListener (ExplorerTree explorerTree) {m_explorerTree = explorerTree;}

	public void dragOver(DragSourceDragEvent dsde) {
//		LogHelper.info("--- ExplorerDragSourceListener:dragOver; DragSourceDragEvent");
	}
	public void dragEnter(DragSourceDragEvent dsde) {
//		LogHelper.info("--- ExplorerDragSourceListener:dragEnter; DragSourceDragEvent");
	}
	public void dragExit(DragSourceEvent dse) {
//		LogHelper.info("--- ExplorerDragSourceListener:dragExit; DragSourceEvent");
	}

	public void dragGestureRecognized (DragGestureEvent dge) {
		LogHelper.info(">>> ExplorerDragSourceListener:dragGestureRecognized");
		NodeItemInfo nodeItemInfo = m_explorerTree.getNodeItemInfo();
		NodeItemInfo.show (0, nodeItemInfo);
		Transferable transferable = new DragFileListTransferable (nodeItemInfo);
		dge.startDrag (null, transferable, this);
		LogHelper.info("<<< ExplorerDragSourceListener:dragGestureRecognized");
	}

	public void dropActionChanged (DragSourceDragEvent dsde) {
		LogHelper.info (">>> ExplorerDragSourceListener::dropActionChanged");
		LogHelper.info ("Action: " + dsde.getDropAction());
		LogHelper.info ("Target Action: " + dsde.getTargetActions());
		LogHelper.info ("User Action: " + dsde.getUserAction());
		LogHelper.info ("<<< ExplorerDragSourceListener::dropActionChanged");
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		LogHelper.info (">>> ExplorerDragSourceListener::Drop Action: " + dsde.getDropAction());
		if (dsde.getDropSuccess()) {
			LogHelper.info ("Drop succeeded; action "+dsde.getDropAction());
		}
		LogHelper.info ("<<< ExplorerDragSourceListener::Drop Action: " + dsde.getDropAction());
	}
}
