package com.dream.repository.quotation;

import com.dream.entity.quotation.Quotation;
import com.dream.entity.quotation.QuotationFile;
import com.wonders.xlab.framework.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Knight on 2015/7/6 15:39.
 */
public interface QuotationFileRepository extends MyRepository<QuotationFile,Long> {

    @Query("from QuotationFile qf where qf.quotation = :quotation ")
    List<QuotationFile> findByQuotation(@Param("quotation") Quotation quotation);

}
