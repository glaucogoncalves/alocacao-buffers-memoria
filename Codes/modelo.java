package modeloTcc;
import ilog.concert.*;
import ilog.cplex.*;

public class modelo 
{
	public void solveMe()
	{
		//quantidade de buffers
		int qtdBuffers = 4;
		int qtdMemoria = 2;
		
		int[] capacidadeMemoria   = {6,7};
		int[] larguraBandaMemoria = {80,80};
		int[] qtdPortasMemoria    = {6,7};
		
		int[] tamanhoBuffer       = {3,3,2,3};
		int[] taxaDeAcessoBuffer  = {4,2,1,4};
		int[] qtdPortasBuffer     = {3,2,2,3};
		
		try {
			//definindo o modelo
			IloCplex cplex = new IloCplex();
						
			IloNumVar[][] x = new IloNumVar[qtdBuffers][];
	        for(int i = 0; i < qtdBuffers; i++)
	            x[i] = cplex.boolVarArray(qtdMemoria);
	        
	        IloNumVar T = cplex.numVar(0.01,1);
	        
	        //primeira equação
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)tamanhoBuffer[i], x[i][j]);
	            cplex.addLe(v, capacidadeMemoria[j]);
	        }
	        //segunda equação
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)taxaDeAcessoBuffer[i], x[i][j]);
	            //cplex.addLe(cplex.prod(f, v),larguraBandaMemoria[j]);
	            //cplex.addLe(v,larguraBandaMemoria[j]);	
	            cplex.addLe(v,cplex.prod(larguraBandaMemoria[j], T));
	        }
	        //terceira equação
	        for(int j = 0;j<qtdMemoria;j++)
	        {
	        	IloLinearNumExpr v = cplex.linearNumExpr();
	            for(int i = 0; i < qtdBuffers; i++)
	              v.addTerm((double)qtdPortasBuffer[i], x[i][j]);
	            cplex.addLe(v, qtdPortasMemoria[j]);
	        }
	        
	        //quarta equação
	        for(int i = 0; i < qtdBuffers; i++)
	            cplex.addEq(cplex.sum(x[i]), 1);

			//função objetivo
	        IloLinearNumExpr obj = cplex.linearNumExpr();
	        obj.addTerm(1.0, T);
	        
	        cplex.addMinimize(obj);
	        System.out.println(cplex.getAlgorithm());
	        System.out.println(cplex.getModel());
	        if(cplex.solve())
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

		} catch (IloException e) {
			e.printStackTrace();
		}
		
	}
	
}
