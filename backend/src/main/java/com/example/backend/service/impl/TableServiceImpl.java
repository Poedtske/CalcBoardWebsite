package com.example.backend.service.impl;

import com.example.backend.exceptions.AppException;
import com.example.backend.model.Event;
import com.example.backend.model.Tafel;
import com.example.backend.repository.TableDao;
import com.example.backend.service.TableService;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TableServiceImpl implements TableService {
    @Autowired
    private TableDao tableRepository;

    @Override
    public Tafel save(Tafel tafel) {
        return tableRepository.save(tafel);
    }

    /**
     * <p><strong>Currently unused</strong></p>
     * @param id
     * @param tafel
     * @return
     */
    @Override
    public Tafel updateById(Long id, Tafel tafel) {
        Tafel t=tableRepository.findById(id).orElseThrow(()->new AppException("Table not found",HttpStatus.NOT_FOUND));
        try{
            t.setSeats(tafel.getSeats());
            return tableRepository.save(t);
        }catch(Exception e){
            throw e;
        }

    }

    /**
     * <p><strong>Currently unused</strong></p>
     * @return
     */
    @Override
    public Iterable<Tafel> findAll() {
        return tableRepository.findAll();
    }

    @Override
    public Tafel findById(Long id) {
        return tableRepository.findById(id).orElseThrow(()->new AppException("Table not found",HttpStatus.NOT_FOUND));
    }

    /**
     * <p><strong>Currently unused</strong></p>
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        tableRepository.deleteById(id);
    }

    /**Creates tables for an event
     *
     * <p>
     *     Is called when creating an event, when layout is set rijen and kolommen are also set.
     *     These are multiplied to know the amount of tables must be created (and for the layout
     *     in the frontend)
     *     Seats variable is set by the event seatsPerTable
     * </p>
     *
     * @param e {@link Event}
     */
    @Override
    public void CreateTables(Event e){
        for(int i = e.getTables().size(); i<(e.getKolommen()*e.getRijen()); i++){
            Tafel t=new Tafel(e,e.getSeatsPerTable());
            tableRepository.save(t);
            e.AddTable(t);
        }
    }

    /**Creates tables for an event
     *
     * <p>
     *     Is called when updating an event, when layout is updated it's decided if you need more or less tables.
     *     If you need more tables this function is called.
     *
     * </p>
     * @param e updated event
     * @param amount amount of tables the event needs more
     * @see #CreateTables(Event)
     * @see #RemoveTables(Event, int)
     */
    @Override
    public void CreateTables(Event e, int amount){
        for(int i = 0; i<Math.abs(amount); i++){
            Tafel t=new Tafel(e,e.getSeatsPerTable());
            tableRepository.save(t);
            e.AddTable(t);
        }
    }

    /**Deletes tables for an event
     *
     * <p>
     *     Is called when updating an event, when layout is updated it's decided if you need more or less tables.
     *     If you need less tables this function is called.
     *
     * </p>
     * @param e updated event
     * @param amount amount of tables the event needs less
     * @see #CreateTables(Event, int)
     */
    @Override
    public void RemoveTables(Event e, int amount){
        int arrayLength=e.getTables().size();
        List<Tafel> tables=e.getTables();
        List<Long> idList=new ArrayList<>();
        for(int i=e.getTables().size()-1;i>=arrayLength-amount;i--){
            Long id=tables.get(i).getId();
            idList.add(id);
        }
        tableRepository.deleteAllById(idList);
    }
}
