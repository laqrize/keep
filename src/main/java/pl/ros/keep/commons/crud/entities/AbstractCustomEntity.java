package pl.ros.keep.commons.crud.entities;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.ros.keep.commons.crud.enums.CrudOperation;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.core.jpa.users.AppUser;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractCustomEntity<ID> {
    protected String status;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    protected AppUser createdBy;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    protected AppUser updatedBy;
    protected Long version;

    public abstract ID getId();

    public abstract void setId(ID id);

    public void updateState(Long operatorId, CrudOperation operation) {
        updateVersion(operation);
        switch (operation) {
            case CREATE:
                this.status = EntityStatus.CURRENT.getCode();
                this.createdAt = LocalDateTime.now();
                this.createdBy = AppUser.builder().id(operatorId).build();
                break;
            case DELETE:
                this.updatedAt = LocalDateTime.now();
                this.status = EntityStatus.DELETED.getCode();
                break;
            case UPDATE:
                this.updatedAt = LocalDateTime.now();
                this.updatedBy = AppUser.builder().id(operatorId).build();
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }

    public void setStatus(EntityStatus status) {
        this.status = status != null ? status.getCode() : null;
    }

    private void updateVersion(CrudOperation operation) {
        if (operation == CrudOperation.CREATE) {
            this.version = 1L;
        } else {
            this.version++;
        }
    }

}
