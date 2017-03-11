
package com.idc.excel;

public class Totals {
	private int tasksFront;
	private int tasksBack;
	private int tasksOther;
	private int hoursFront;
	private int hoursBack;
	private int hoursOther;
	private int hoursQcrFront;
	private int hoursQcrBack;
	private int hoursQcrOther;

	public int getTasksFront() {return tasksFront;}
	public int getTasksBack() {return tasksBack;}
	public int getTasksOther() {return tasksOther;}
	public int getAllTasks() {return tasksFront+tasksBack+tasksOther;}
	
	public int getHoursFront() {return hoursFront;}
	public int getHoursQcrFront() {return hoursQcrFront;}
	public int getTotalHoursFront() {return hoursFront+hoursQcrFront;}

	public int getHoursBack() {return hoursBack;}
	public int getHoursQcrBack() {return hoursQcrBack;}
	public int getTotalHoursBack() {return hoursBack+hoursQcrBack;}

	public int getHoursOther() {return hoursOther;}
	public int getHoursQcrOther() {return hoursQcrOther;}
	public int getTotalHoursOther() {return hoursOther+hoursQcrOther;}

	public int getAllHours() {return hoursFront+hoursBack+hoursOther;}
	public int getAllQcrHours() {return hoursQcrFront+hoursQcrBack+hoursQcrOther;}

	public int getTotalHours() {return getAllHours()+getAllQcrHours();}

	public void setTasksFront (int tasksFront) {this.tasksFront = tasksFront;}
	public void setTasksBack (int tasksBack) {this.tasksBack = tasksBack;}
	public void setTasksOther (int tasksOther) {this.tasksOther = tasksOther;}
	public void setHoursFront (int hoursFront) {this.hoursFront = hoursFront;}
	public void setHoursBack (int hoursBack) {this.hoursBack = hoursBack;}
	public void setHoursOther (int hoursOther) {this.hoursOther = hoursOther;}
	public void setHoursQcrFront (int hoursQcrFront) {this.hoursQcrFront = hoursQcrFront;}
	public void setHoursQcrBack (int hoursQcrBack) {this.hoursQcrBack = hoursQcrBack;}
	public void setHoursQcrOther (int hoursQcrOther) {this.hoursQcrOther = hoursQcrOther;}

	public void setTotals (Task task) {
		String owner = task.getOwner();
		int dur = makeDur (task.getDur());
		int qcr = makeDur (task.getQcr());
		if (owner == null || owner.length() < 4) {
			tasksOther++;
			hoursOther += dur; hoursQcrOther += qcr;
		}
		else if (owner.substring(0,4).toLowerCase().equals("fron")) {
			tasksFront++;
			hoursFront += dur; hoursQcrFront += qcr;
		}
		else if (owner.substring(0,4).toLowerCase().equals("back")) {
			tasksBack++;
			hoursBack += dur; hoursQcrBack += qcr;
		}
		else {
			tasksOther++;
			hoursOther += dur; hoursQcrOther += qcr;
		}
	}
	private int makeDur (String strDur) {
		int dur;
		if (strDur == null || strDur.equals("")) {
			dur = 0;
		}
		else {
			double dbl = Double.parseDouble(strDur) * 8;
			dur = (int) dbl;
		}
//		System.out.println("Dur; str :"+strDur+": int "+dur);
		return dur;
	}
	public void incrementTotals (Totals subtotals) {
//		System.out.println(">>> Totals::incrementTotals");
//		subtotals.show("Totals::incrementTotals - subtotal");
//		this.show("Totals::incrementTotals - before increment");
		this.tasksFront += subtotals.getTasksFront();
		this.tasksBack += subtotals.getTasksBack();
		this.tasksOther += subtotals.getTasksOther();
		this.hoursFront += subtotals.getHoursFront();
		this.hoursBack += subtotals.getHoursBack();
		this.hoursOther += subtotals.getHoursOther();
		this.hoursQcrFront += subtotals.getHoursQcrFront();
		this.hoursQcrBack += subtotals.getHoursQcrBack();
		this.hoursQcrOther += subtotals.getHoursQcrOther();
//		this.show("Totals::incrementTotals - after increment");
//		System.out.println("<<< Totals::incrementTotals");
	}
	public void show(String msg) {
		System.out.println(">>> Totals::show; "+msg);
		System.out.print("Front: ");
		System.out.println("Tasks "+getTasksFront()+" hours "+getHoursFront()+
					" qcr "+getHoursQcrFront());
		System.out.print("Back: ");
		System.out.println("Tasks "+getTasksBack()+" hours "+getHoursBack()+
				" qcr "+getHoursQcrBack());
		System.out.print("Other: ");
		System.out.println("Tasks "+getTasksOther()+" hours "+getHoursOther()+
				" qcr "+getHoursQcrOther());
		System.out.println("<<< Totals::show; "+msg);
	}
}






