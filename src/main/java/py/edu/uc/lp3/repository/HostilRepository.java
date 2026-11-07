package py.edu.uc.lp3.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.domain.minecraft.Hostil;

public interface HostilRepository extends JpaRepository<Hostil, Long> {
    List<Hostil> findAllByOrderByIdAsc();
}
