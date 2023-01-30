package br.com.augusto.votin_on.repositories;

import br.com.augusto.votin_on.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    Optional<List<Vote>> findByGuideId(Long id);
    Optional<Vote> findByGuideIdAndUserCpf(Long id,String cpf);

}
