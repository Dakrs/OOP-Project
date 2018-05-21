import java.util.Set;
import java.util.Comparator;
import java.util.Map;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Collections;

public class BDgeral
{
    private BDEmpresas empresas;
    private BDIndividuais individuais;
    private BDFaturas faturas;
    private BDSetores setores;
    
    
    public static void createFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                FileOutputStream writer = new FileOutputStream(path);
                writer.write(("").getBytes());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public BDgeral(){
        this.empresas       = new BDEmpresas();
        this.individuais    = new BDIndividuais();
        this.faturas        = new BDFaturas();
        this.setores        = new BDSetores();
    }
    
    public BDgeral(BDEmpresas a,BDIndividuais b,BDFaturas c, BDSetores d){
        this.empresas       = a.clone();
        this.individuais    = b.clone();
        this.faturas        = c.clone();
        this.setores        = d.clone();
    }
    
    public BDgeral(BDgeral a){
        this.empresas       = a.getBDEmpresas();
        this.individuais    = a.getBDIndividuais();
        this.faturas        = a.getBDFaturas();
        this.setores        = a.getBDSetores();
    }
    
    public void guardaEstado(String nome) throws FileNotFoundException ,IOException{
      //  createFile(nome);
        FileOutputStream fos = new FileOutputStream(nome);

        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
    }
    
    public BDgeral carregaEstado(String nome) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(nome);
        ObjectInputStream ois = new ObjectInputStream(fis);
        BDgeral h = (BDgeral) ois.readObject();
        ois.close();
        return h;
    }
    
    
    public BDEmpresas getBDEmpresas(){
        return this.empresas.clone();
    }
    
    public BDIndividuais getBDIndividuais(){
        return this.individuais.clone();
    }
    
    public BDFaturas getBDFaturas(){
        return this.faturas.clone();
    }

    public BDSetores getBDSetores() {
        return this.setores.clone();
    }
    
    public void addIndividual(CIndividual i){
        this.individuais.addContribuinte(i);
    }
    
    public void addEmpresa(Empresa i){
        this.empresas.addContribuinte(i);
    }
    
    public void addFatura(Fatura i){
        this.faturas.addFatura(i,this.individuais,this.empresas);
    }

    public void addSetor(Setor s){this.setores.addSetor(s);}
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.individuais.toString() +"\n");
        sb.append(this.empresas.toString() + "\n");
        sb.append(this.faturas.toString() + "\n");
        sb.append(this.setores.toString() + "\n");
        
        return sb.toString();
    }
    
    
    //7
    public List<Fatura> listagem_ordenada_emp_fatura(LocalDate start,LocalDate end, int type, int id){
        Empresa e;
        try {
            e = (Empresa) this.empresas.getContribuinte(id);
        }
        catch (Erros aux){
            System.out.println("Empresa nao encontrada");
            return new ArrayList<Fatura>();
        }
        
        List<Fatura> list = this.faturas.faturas_no_intervalo(start,end,e.getFaturas());
        
        if (type == 1){
            Collections.sort(list, new Comparator<Fatura>(){
                
                public int compare(Fatura f1,Fatura f2){
                    if (f1.getEmissao().isAfter(f2.getEmissao()))
                        return -1;
                    else if (f1.getEmissao().equals(f2.getEmissao()))
                        return 0;
                    return 1;
                }
            });
        }
        else if (type == 0){
            Collections.sort(list, new Comparator<Fatura>(){
                
                public int compare(Fatura f1,Fatura f2){
                    if (f1.getValor() < f2.getValor())
                        return -1;
                    else if (f1.getValor() == f2.getValor())
                        return 0;
                    return 1;
                }
            });
        }
        
        return list;
    }
    
    //8
    public Map<Integer,List<Fatura>> listagem_cont_fatura_time(LocalDate start,LocalDate end, int id){
        Empresa e;
        Map <Integer,List<Fatura>> listagem = new HashMap<>();
        try {
            e = (Empresa) this.empresas.getContribuinte(id);
        }
        catch (Erros aux){
            System.out.println("Empresa nao encontrada");
            return listagem;
        }
        
        List<Fatura> list = this.faturas.faturas_no_intervalo(start,end,e.getFaturas());
        List<Fatura> aux1;
        
        for(Fatura f : list){
            if (listagem.containsKey(f.getNif_cliente())){
                aux1 = new ArrayList<>();
                listagem.put(f.getNif_cliente(),aux1);
            }
            aux1 = listagem.get(f.getNif_cliente());
            aux1.add(f);
        }
        
        return listagem;
    }
    
    //9
    
    public Map<Integer,List<Fatura>> listagem_cont_fatura(int id){
        Empresa e;
        Map <Integer,List<Fatura>> listagem = new HashMap<>();
        try {
            e = (Empresa) this.empresas.getContribuinte(id);
        }
        catch (Erros aux){
            System.out.println("Empresa nao encontrada");
            return listagem;
        }
        
        List<Fatura> list = this.faturas.faturas_contribuinte(e.getFaturas());
        List<Fatura> aux1;
        
        for(Fatura f : list){
            if (listagem.containsKey(f.getNif_cliente())){
                aux1 = new ArrayList<>();
                listagem.put(f.getNif_cliente(),aux1);
            }
            aux1 = listagem.get(f.getNif_cliente());
            aux1.add(f);
        }
        
        for(List<Fatura> a : listagem.values()){
           Collections.sort(a, new Comparator<Fatura>(){
                
                public int compare(Fatura f1,Fatura f2){
                    if (f1.getValor() < f2.getValor())
                        return -1;
                    else if (f1.getValor() == f2.getValor())
                        return 0;
                    return 1;
                }
            });
        }
        
        return listagem;
    }
    //10
    public double total_faturado(LocalDate start, LocalDate end ,int id){
        Empresa e;
        try {
            e = (Empresa) this.empresas.getContribuinte(id);
        }
        catch (Erros aux){
            System.out.println("Empresa nao encontrada");
            return -1;
        }
        
        List<Fatura> list = this.faturas.faturas_no_intervalo(start,end,e.getFaturas());
        
        return list.stream()
                   .mapToDouble( b -> b.getValor())
                   .sum();
    }
    //11
    public double rel_top10(){
        Map <Integer,List<Fatura>> listagem = new HashMap<>();
        
        List<Fatura> aux = new ArrayList<>();
        
        for(Fatura a : faturas.getFaturas().values()){
            if (!listagem.containsKey(a.getNif_cliente())){
                aux = new ArrayList<>();
                listagem.put(a.getNif_cliente(),aux);
            }
            aux = listagem.get(a.getNif_cliente());
            aux.add(a);
        }
        
        List <Double> aux2 = new ArrayList <>();
        
        listagem.forEach((k,v) -> aux2.add(v.stream().mapToDouble(b -> b.getValor()).sum()));
        
        Collections.sort(aux2, new Comparator<Double>(){
                
                public int compare(Double f1,Double f2){
                    if (f2 == f1)
                        return 0;
                    return f2 > f1 ? -1 : 1;
                }
            });
        
            
        int i = 0;
        double top10_total = 0;
        double total = 0;
        
        for(Double k : aux2){
            if (i < 10){
                i++;
                top10_total += k;
            }
            total += k;
        }
        
        return top10_total / total;
    }
    
}