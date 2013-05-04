
	@Override
	public #returnType# #methodName#(#arglist#) {
		#Core# core;
		try {
			core = (#Core#)this.clone();
		}catch(CloneNotSupportedException e){
			core = this;
		}
		#setterBody#
		return core;
	}
