package bgu.spl.Assignment3.Reactor;

import bgu.spl.Assignment3.Command;
import bgu.spl.Assignment3.TBGProtocol;

public class TBGProtocol4Reactor extends TBGProtocol implements AsyncServerProtocol<Command> {

	@Override
	public boolean shouldClose() {
		if(this.quit)
			return true;
		return false;
	}

	@Override
	public void connectionTerminated() {
		// TODO Auto-generated method stub

	}

}
