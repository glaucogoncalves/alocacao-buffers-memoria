import ilog.concert.*;
import ilog.cplex.*;

public class Modelo 
{
	public boolean solveMe(int[] tamanhoBuffer, int[] taxaDeAcessoBuffer, int[] qtdPortasBuffer,
						int[] capacidadeMemoria, int[] larguraBandaMemoria, int[] qtdPortasMemoria,
						int qtdBuffers, int qtdMemoria)
	{
		//quantidade de buffers
//		int qtdBuffers = 4;
//		int qtdMemoria = 2;
		
//		int[] capacidadeMemoria   = {6,7};
//		int[] larguraBandaMemoria = {80,80};
//		int[] qtdPortasMemoria    = {6,7};
		
//		int[] tamanhoBuffer       = {3,3,2,3};
//		int[] taxaDeAcessoBuffer  = {4,2,1,4};
//		int[] qtdPortasBuffer     = {3,2,2,3};
		boolean result = false;
		
		try {
			//definindo o modelo
			IloCplex cplex = new IloCplex();
						
			IloNumVar[][] x = new IloNumVar[qtdBuffers][];
	        for(int i = 0; i < qtdBuffers; i++)
	            x[i] = cplex.boolVarArray(qtdMemoria);
	        
	        IloNumVar T = cplex.numVar(0.01,1);
	        
	        //primeira equacao
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)tamanhoBuffer[i], x[i][j]);
	            cplex.addLe(v, capacidadeMemoria[j]);
	        }
	        //segunda equacao
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)taxaDeAcessoBuffer[i], x[i][j]);
	            //cplex.addLe(cplex.prod(f, v),larguraBandaMemoria[j]);
	            //cplex.addLe(v,larguraBandaMemoria[j]);	
	            cplex.addLe(v,cplex.prod(larguraBandaMemoria[j], T));
	        }
	        //terceira equacao
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)qtdPortasBuffer[i], x[i][j]);
	            cplex.addLe(v, qtdPortasMemoria[j]);
	        }
	        
	        // quarta equacao
	        for(int i = 0; i < qtdBuffers; i++)
	            cplex.addEq(cplex.sum(x[i]), 1);

			// funcao objetivo
	        IloLinearNumExpr obj = cplex.linearNumExpr();
	        obj.addTerm(1.0, T);
	        
	        cplex.addMinimize(obj);
	        // System.out.println(cplex.getAlgorithm());
	        // System.out.println(cplex.getModel());
	        result = cplex.solve();
	        System.out.println(cplex.getCplexTime());
	        if(result)
			{
				System.out.println("obj = "+cplex.getObjValue());
				//System.out.println("f = "+cplex.getValue(f));
				for(int i=0;i<qtdBuffers;i++)
				{
					for(int j=0;j<qtdMemoria;j++)
					{
						System.out.println("x["+i+"]["+j+"] :"+cplex.getValue(x[i][j]));
					}
				}
			}
	        else {
	        	System.out.println("Infeasible");
	        }

		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}	
}
