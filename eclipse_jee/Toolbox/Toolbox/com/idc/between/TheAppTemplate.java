package com.idc.between;

public abstract class TheAppTemplate {

	abstract public boolean isAppOver();
	abstract public void setAppOver (boolean b);

	abstract public void doTask();
	abstract public void setStarted();
	abstract public void setStopped();
	abstract public void addMessage (String msg);
	abstract public void setProgressMessage (String msg);

	abstract public void initProgressBar (int iMin, int iMax);
	abstract public void setProgressBar (int value);
	abstract public void setProgressBar ();
	abstract public void setMaxProgressBar ();

	abstract public boolean isTheAppStopped();
}
