package com.denwill.kotlin.bristolsalary

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denwill.kotlin.bristolsalary.databinding.SalaryEditBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class EditFragment : Fragment() {
    private var binding: SalaryEditBinding? = null
    private val args: EditFragmentArgs by navArgs()
    private val motor: SingleModelMotor by viewModel { parametersOf(args.modelId) }
    private var jobDate = Instant.now()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = SalaryEditBinding.inflate(inflater, container, false)
        .apply { binding = this}
        .root
fun convertInstant(instant:Instant):String{
    val PATTERN_FORMAT = "MM.dd.yyyy"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant).toString()
}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.let { it->
            it.date.text = convertInstant(Instant.now())
            it.dateButton.setOnClickListener{setDate()}
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.countWorkers.min = 1
            }
            it.countWorkers.max = 5
            it.countWorkers.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                it.countWorkersText.text = p1.toString()
                if(isCompleted()==true) {
                    calculateSalary()
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
            var watcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if(isCompleted()==true) {
                        calculateSalary()
                    }
                }

            }
            it.revenue.addTextChangedListener(watcher)
            it.baseSalary.addTextChangedListener(watcher)

        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            motor.states.collect { state ->
                if (savedInstanceState == null) {
                    state.item?.let {
                        binding?.apply {
                            date.setText(convertInstant(it.date))
                            revenue.setText(it.revenue.toString())
                            countWorkers.progress = it.countWorkers
                            baseSalary.setText(it.baseSalary.toString())
                            salary.setText(it.salary.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null

        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actions_edit, menu)
        menu.findItem(R.id.delete).isVisible = args.modelId != null

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                save()
                return true
            }
            R.id.delete -> {
                delete()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
    private fun isCompleted() : Boolean? = binding?.run{
        date.text.isNotEmpty()&& revenue.text.isNotEmpty()&&baseSalary.text.isNotEmpty()
    }
    private fun calculateSalary() {
        try {
            binding?.let { var salary =((it.revenue.text.toString()).toDouble() /
                it.countWorkersText.text.toString().toInt())/
                100.toDouble()+it.baseSalary.text.toString().toDouble().
                let {it2->it2 - it2*0.13 }.toDouble()
                it.salary.text = BigDecimal(salary).setScale(2, RoundingMode.HALF_EVEN).toString()
            }
        }
        catch (ex: Exception) {
            Toast.makeText(activity, "Пиши нормально", Toast.LENGTH_SHORT).show()
        }
    }
    private fun save() {
        if(isCompleted() == true) {
            binding?.apply {
                val model = motor.states.value.item
                val edited = model?.copy(
                    date = jobDate,
                    revenue = revenue.text.toString().toDouble(),
                    baseSalary = baseSalary.text.toString().toDouble(),
                    salary = salary.text.toString().toDouble(),
                    countWorkers = countWorkersText.text.toString().toInt()
                ) ?: SalaryModel(
                    date = jobDate,
                    revenue = revenue.text.toString().toDouble(),
                    baseSalary = baseSalary.text.toString().toDouble(),
                    salary = salary.text.toString().toDouble(),
                    countWorkers = countWorkersText.text.toString().toInt()
                )

                edited.let { motor.save(it) }
            }

            navToDisplay()
        }
    }
    private fun setDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        jobDate = c.toInstant()

        val dpd =
            activity?.let {
                DatePickerDialog(it, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    binding?.date?.setText("$monthOfYear.$dayOfMonth.$year")
                }, year, month, day)
            }

        dpd?.show()
    }
    private fun setWorkers() {

    }
    private fun delete() {
        val model = motor.states.value.item

        model?.let { motor.delete(it) }
        navToList()
    }

    private fun navToDisplay() {
        hideKeyboard()
        findNavController().popBackStack()
    }

    private fun navToList() {
        hideKeyboard()
        findNavController().popBackStack(R.id.rosterListFragment, false)
    }

    private fun hideKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()

            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}