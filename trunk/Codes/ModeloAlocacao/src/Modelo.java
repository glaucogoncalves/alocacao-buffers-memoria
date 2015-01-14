import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import ilog.concert.*;
import ilog.cplex.*;

public class Modelo 
{
	private double ultimaFrequencia = 0;
	private double ultimoPeriodo = 0;
	private double startTime = 0;
	private double endTime = 0;
	
	public boolean solveMe(int[] tamanhoBuffer, int[] taxaDeAcessoBuffer, int[] qtdPortasBuffer,
						int[] capacidadeMemoria, int[] larguraBandaMemoria, int[] qtdPortasMemoria,
						int qtdBuffers, int qtdMemoria, int idProblema)
	{
		Path arquivoLog = Paths.get("log_problema_pesado_" + idProblema + ".txt");
		ArrayList<String> logLinhas = new ArrayList<String>(); 
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
	        
	        IloNumVar T = cplex.numVar(0.0025,1);
	        
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
	        startTime = cplex.getCplexTime();
	        result = cplex.solve();
	        endTime = cplex.getCplexTime();
	        System.out.println(cplex.getCplexTime());
	        logLinhas.add("tempo de execucao(s): " + cplex.getCplexTime());
	        if(result)
			{
				System.out.println("obj = "+cplex.getObjValue());
				logLinhas.add("obj = "+cplex.getObjValue());
				ultimoPeriodo = cplex.getObjValue();
				ultimaFrequencia = 1/ultimoPeriodo;			
				//System.out.println("f = "+cplex.getValue(f));
				for(int i=0;i<qtdBuffers;i++)
				{
					for(int j=0;j<qtdMemoria;j++)
					{
						System.out.println("x["+i+"]["+j+"] :"+cplex.getValue(x[i][j]));
						logLinhas.add("x["+i+"]["+j+"] :"+cplex.getValue(x[i][j]));
					}
				}
			}
	        else {
	        	System.out.println("Infeasible");
	        	logLinhas.add("Infeasible");
	        }

		} catch (IloException e) {
			e.printStackTrace();
		}
		try {
			Files.write(arquivoLog, logLinhas, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}	
	
	public double getFrequencia() {
		return ultimaFrequencia;
	}
	
	public double getPeriodo() {
		return ultimoPeriodo;
	}
	
	public double getTime() {
		return (endTime-startTime);
	}
}
