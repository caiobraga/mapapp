package com.caiobraga.mapapp;

import static com.caiobraga.mapapp.MainActivity.getAppContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BancoDadosSingleton {

        protected SQLiteDatabase db;
        private final String NOME_BANCO = "map_bd";
        private static BancoDadosSingleton INSTANCE;

        private  final String[] SCRIPT_DATABASE_CREATE = new String[] {
                "CREATE TABLE Location (id INTEGER PRIMARY KEY AUTOINCREMENT, descricao TEXT NOT NULL,latitude TEXT NOT NULL, longitude TEXT NOT NULL)",
                "CREATE TABLE logs (id INTEGER PRIMARY KEY AUTOINCREMENT, msg TEXT NOT NULL, timestamp TEXT, id_location INTEGER, FOREIGN KEY (id_location) REFERENCES Location (id" +
                        "))"

        };

    private BancoDadosSingleton(Context ctx) {
        db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        Cursor c = buscar("sqlite_master", null, "type = 'table'", "");
        if(c.getCount() == 1){
            for(int i = 0; i < SCRIPT_DATABASE_CREATE.length; i++){
                db.execSQL(SCRIPT_DATABASE_CREATE[i]);
            }
            Log.i("BANCO_DADOS", "Criou tabelas do banco e as populou.");
        }
        c.close();
        Log.i("BANCO_DADOS", "Abriu conexão com o banco.");
    }


    // Insere um novo registro
        public long inserir(String tabela, ContentValues valores) {
            long id = db.insert(tabela, null, valores);
            Log.i("BANCO_DADOS", "Cadastrou registro com o id [" + id + "]");
            return id;
        }
        // Atualiza registros
        public int atualizar(String tabela, ContentValues valores, String where) {
            int count = db.update(tabela, valores, where, null);
            Log.i("BANCO_DADOS", "Atualizou [" + count + "] registros");
            return count;
        }
        // Deleta registros
        public int deletar(String tabela, String where) {
            int count = db.delete(tabela, where, null);
            Log.i("BANCO_DADOS", "Deletou [" + count + "] registros");
            return count;
        }

        public Cursor buscar(String tabela, String colunas[], String where, String orderBy) {
            Cursor c;
            if(!where.equals(""))
                c = db.query(tabela, colunas, where, null, null, null, orderBy);
            else
                c = db.query(tabela, colunas, null, null, null, null, orderBy);
            Log.i("BANCO_DADOS", "Realizou uma busca e retornou [" + c.getCount() + "] registros.");
            return c;
        }
        // Abre conexão com o banco
        private void abrir() {
            Context ctx = getAppContext();
            if(!db.isOpen()){
                // Abre o banco de dados já existente
                db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
                Log.i("BANCO_DADOS", "Abriu conexão com o banco.");
            }else{
                Log.i("BANCO_DADOS","Conexão com o banco já estava aberta.");
            }
        }

        public static BancoDadosSingleton getInstance(){
            if(INSTANCE == null)
                INSTANCE = new BancoDadosSingleton(getAppContext());
            INSTANCE.abrir(); //abre conexão se estiver fechada
            return INSTANCE;
        }
        // Fecha conexão com o banco
        public void fechar() {
            if (db != null && db.isOpen()) {
                db.close();
                Log.i("BANCO_DADOS", "Fechou conexão com o Banco.");
            }
        }

    }


