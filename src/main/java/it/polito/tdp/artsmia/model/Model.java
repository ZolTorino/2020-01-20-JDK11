package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;



public class Model {
	
	ArtsmiaDAO dao;
	private SimpleWeightedGraph<Artist, DefaultWeightedEdge> grafo;
	private Map<Integer, Artist> idMap;
	
	public Model() {
		dao= new ArtsmiaDAO();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMap=new HashMap<Integer, Artist>();
	}
	public List<String> roles(){
		LinkedList<String> out= new LinkedList<String>(dao.roles());
		return out;
	}
	public String creaGrafo(String ruolo) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMap=new HashMap<Integer, Artist>();
		String s="";
		dao.vertices(ruolo, idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		s+="Vertici: "+grafo.vertexSet().size();
		LinkedList<Arco>archi= new LinkedList<Arco>(dao.edges(ruolo, idMap));
		for(Arco a:archi)
		{
			if(grafo.containsVertex(a.getA1()) && grafo.containsVertex(a.getA2())) {
					Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
				
			}
		}
		s+="Archi: "+grafo.edgeSet().size()+"\n";

		return s;
	}
	Map<Integer,Artist> percorso=new HashMap<Integer,Artist>();

	int pesobest=0;
	public String calcolaPercorso(String in) {
		String s="";
		
		if(!idMap.containsKey(Integer.parseInt(in)))
		{
			return "Artista non trovato";
		}
			LinkedList<Artist> parziale= new LinkedList<Artist>();
			Artist a= idMap.get(Integer.parseInt(in));
			percorso=new HashMap<Integer,Artist>();
			parziale.add(a);
			calcola(parziale,-1);
			s+="Peso: "+pesobest+" Artisti: \n";
			for(Artist c: percorso.values())
			{
				s+=c.getName()+"\n";
			}
			return s;
	}
	
	public void calcola(LinkedList<Artist>parziale, int peso)
	{
		int pesoattuale=peso;
			Artist a=parziale.get(parziale.size()-1);
			if(parziale.size()>percorso.size())
			{
				percorso.clear();
				for(Artist b: parziale)
				{
					pesobest=peso;
					percorso.put(b.getId(), b);
				}
			}
		for(Artist iter: Graphs.neighborListOf(grafo, a))
		{
			System.out.println(parziale.size());
			if(parziale.size()==1)
			{
				
				pesoattuale=(int)grafo.getEdgeWeight(grafo.getEdge(a, iter));
				System.out.println("Analisi peso "+pesoattuale);
			
			}
			if(grafo.getEdgeWeight(grafo.getEdge(a, iter))==pesoattuale&&!parziale.contains(iter))
			{
				parziale.add(iter);
				calcola(parziale,pesoattuale);
				parziale.remove(iter);
				
			}
		}
		
	}
}
