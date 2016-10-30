package com.ozguryazilim.tekir.core.tax;

import com.ozguryazilim.telve.forms.ParamEdit;
import com.ozguryazilim.telve.forms.ParamBase;
import com.ozguryazilim.tekir.entities.TaxDefinition;
import javax.inject.Inject;

/**
 * Home Control Class
 * 
 * @author
 */
@ParamEdit
public class TaxDefinitionHome extends ParamBase<TaxDefinition, Long> {

	@Inject
	private TaxDefinitionRepository repository;

	public TaxDefinitionRepository getRepository() {
		return this.repository;
	}

	public void setRepository(final TaxDefinitionRepository repository) {
		this.repository = repository;
	}
}